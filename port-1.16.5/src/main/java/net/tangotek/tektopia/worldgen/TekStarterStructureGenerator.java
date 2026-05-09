package net.tangotek.tektopia.worldgen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.JigsawReplacementStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.common.TekItemMeta;
import net.tangotek.tektopia.registry.TekItems;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.village.TekVillage;

public final class TekStarterStructureGenerator {
    private TekStarterStructureGenerator() {
    }

    public static boolean isStarterType(TekStructureType type) {
        return type == TekStructureType.TOWNHALL
                || type == TekStructureType.STORAGE
                || type == TekStructureType.HOME
                || type == TekStructureType.FARM
                || type == TekStructureType.MINESHAFT
                || type == TekStructureType.SCHOOL
                || type == TekStructureType.TAVERN
                || type == TekStructureType.LIBRARY;
    }

    public static Result generate(ServerWorld level, TekStructureType type, BlockPos near, Direction facing) {
        return generate(level, type, near, facing, null);
    }

    public static Result generate(ServerWorld level, TekStructureType type, BlockPos near, Direction facing, TekVillage village) {
        Direction requestedFacing = facing.getAxis().isHorizontal() ? facing : Direction.SOUTH;
        VanillaTemplate templateInfo = VanillaTemplate.forType(type);
        Rotation rotation = rotationFromSouth(requestedFacing);
        Template template = loadTemplate(level, templateInfo.location);
        BlockPos size = template.getSize(rotation);
        if (size.getX() <= 0 || size.getY() <= 0 || size.getZ() <= 0) {
            TekTopiaPort.LOGGER.warn("TekTopia starter template {} has no size; skipping {}", templateInfo.location, type);
            return Result.failed(type, near, requestedFacing);
        }

        BlockPos surface = level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, near).immutable();
        BlockPos origin = surface.offset(-size.getX() / 2, 0, -size.getZ() / 2).immutable();
        PlacementSettings placement = new PlacementSettings()
                .setRotation(rotation)
                .setIgnoreEntities(true)
                .setFinalizeEntities(false)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK)
                .addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
        Random random = level.getRandom();
        boolean placed = template.placeInWorld(level, origin, origin, placement, random, 3);
        if (!placed) {
            TekTopiaPort.LOGGER.warn("TekTopia starter template {} failed to place at {}", templateInfo.location, origin.toShortString());
            return Result.failed(type, surface, requestedFacing);
        }

        BlockPos templateCenter = origin.offset(size.getX() / 2, Math.max(1, size.getY() / 2), size.getZ() / 2).immutable();
        DoorSelection door = findBestDoor(level, origin, size, templateCenter, requestedFacing);
        if (door == null) {
            door = createFallbackDoor(level, surface, requestedFacing);
        }

        int placedBlocks = Math.max(1, size.getX() * Math.max(1, size.getY()) * size.getZ());
        if (placeMarkerFrame(level, type, door.doorPos, door.signFacing, village)) {
            placedBlocks++;
        }

        return new Result(type, door.doorInside, door.framePos(), door.signFacing, placedBlocks);
    }

    private static Template loadTemplate(ServerWorld level, ResourceLocation location) {
        TemplateManager manager = level.getStructureManager();
        return manager.getOrCreate(location);
    }

    private static DoorSelection findBestDoor(ServerWorld level, BlockPos origin, BlockPos size, BlockPos templateCenter, Direction preferredFacing) {
        List<DoorSelection> doors = new ArrayList<>();
        BlockPos max = origin.offset(Math.max(0, size.getX() - 1), Math.max(0, size.getY() - 1), Math.max(0, size.getZ() - 1));
        for (BlockPos pos : BlockPos.betweenClosed(origin, max)) {
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof DoorBlock) || !isLowerDoor(state)) {
                continue;
            }
            DoorSelection selection = classifyDoor(level, pos.immutable(), templateCenter, preferredFacing);
            if (selection != null) {
                doors.add(selection);
            }
        }
        if (doors.isEmpty()) {
            return null;
        }
        doors.sort(Comparator.comparingInt((DoorSelection door) -> door.score).reversed());
        return doors.get(0);
    }

    @Nullable
    private static DoorSelection classifyDoor(ServerWorld level, BlockPos doorPos, BlockPos templateCenter, Direction preferredFacing) {
        DoorSelection best = null;
        for (Direction insideDirection : Direction.Plane.HORIZONTAL) {
            BlockPos inside = doorPos.relative(insideDirection);
            BlockPos outside = doorPos.relative(insideDirection.getOpposite());
            if (!isPassable(level, inside) || !isPassable(level, outside)) {
                continue;
            }
            int insideDistance = horizontalDistanceSqr(inside, templateCenter);
            int outsideDistance = horizontalDistanceSqr(outside, templateCenter);
            if (insideDistance > outsideDistance) {
                continue;
            }
            Direction signFacing = insideDirection.getOpposite();
            int score = (signFacing == preferredFacing ? 20_000 : 0)
                    - horizontalDistanceSqr(doorPos, templateCenter)
                    - Math.abs(doorPos.getY() - templateCenter.getY()) * 20;
            DoorSelection candidate = new DoorSelection(doorPos, inside.immutable(), signFacing, score);
            if (best == null || candidate.score > best.score) {
                best = candidate;
            }
        }
        return best;
    }

    private static DoorSelection createFallbackDoor(ServerWorld level, BlockPos surface, Direction facing) {
        BlockPos doorPos = surface.relative(facing, 2).immutable();
        BlockPos inside = doorPos.relative(facing.getOpposite()).immutable();
        BlockPos below = doorPos.below();
        setBlock(level, below, Blocks.OAK_PLANKS.defaultBlockState());
        setBlock(level, inside.below(), Blocks.OAK_PLANKS.defaultBlockState());
        setBlock(level, doorPos, face(Blocks.OAK_DOOR.defaultBlockState(), facing));
        setBlock(level, doorPos.above(), face(Blocks.OAK_DOOR.defaultBlockState()
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), facing));
        return new DoorSelection(doorPos, inside, facing, 0);
    }

    private static boolean placeMarkerFrame(ServerWorld level, TekStructureType type, BlockPos doorPos, Direction signFacing, TekVillage village) {
        Item token = tokenFor(type);
        if (token == null) {
            return false;
        }

        BlockPos anchor = doorPos.above(2).immutable();
        if (isPassable(level, anchor)) {
            setBlock(level, anchor, Blocks.OAK_PLANKS.defaultBlockState());
        }

        List<Direction> attempts = orderedFrameDirections(signFacing);
        for (Direction direction : attempts) {
            BlockPos framePos = anchor.relative(direction).immutable();
            if (!isPassable(level, framePos)) {
                setBlock(level, framePos, Blocks.AIR.defaultBlockState());
            }
            ItemFrameEntity frame = new ItemFrameEntity(level, framePos, direction);
            if (!frame.survives()) {
                continue;
            }
            frame.setItem(TekItemMeta.bindToVillage(new ItemStack(token), village));
            if (level.addFreshEntity(frame)) {
                return true;
            }
        }
        return false;
    }

    private static List<Direction> orderedFrameDirections(Direction preferred) {
        List<Direction> directions = new ArrayList<>();
        directions.add(preferred);
        directions.add(preferred.getClockWise());
        directions.add(preferred.getCounterClockWise());
        directions.add(preferred.getOpposite());
        return directions;
    }

    private static int setBlock(ServerWorld level, BlockPos pos, BlockState state) {
        return level.setBlock(pos, state, 3) ? 1 : 0;
    }

    private static BlockState face(BlockState state, Direction facing) {
        return state.hasProperty(HorizontalBlock.FACING) ? state.setValue(HorizontalBlock.FACING, facing) : state;
    }

    private static boolean isLowerDoor(BlockState state) {
        return !state.hasProperty(DoorBlock.HALF) || state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
    }

    private static boolean isPassable(ServerWorld level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getCollisionShape(level, pos).isEmpty();
    }

    private static int horizontalDistanceSqr(BlockPos left, BlockPos right) {
        int dx = left.getX() - right.getX();
        int dz = left.getZ() - right.getZ();
        return dx * dx + dz * dz;
    }

    private static Rotation rotationFromSouth(Direction facing) {
        switch (facing) {
            case WEST:
                return Rotation.CLOCKWISE_90;
            case NORTH:
                return Rotation.CLOCKWISE_180;
            case EAST:
                return Rotation.COUNTERCLOCKWISE_90;
            case SOUTH:
            default:
                return Rotation.NONE;
        }
    }

    private static Item tokenFor(TekStructureType type) {
        switch (type) {
            case TOWNHALL:
                return TekItems.STRUCTURE_TOWNHALL_TOKEN.get();
            case STORAGE:
                return TekItems.STRUCTURE_STORAGE_TOKEN.get();
            case HOME:
                return TekItems.STRUCTURE_HOME_TOKEN.get();
            case FARM:
                return TekItems.STRUCTURE_FARM_TOKEN.get();
            case MINESHAFT:
                return TekItems.STRUCTURE_MINESHAFT_TOKEN.get();
            case SCHOOL:
                return TekItems.STRUCTURE_SCHOOL_TOKEN.get();
            case TAVERN:
                return TekItems.STRUCTURE_TAVERN_TOKEN.get();
            case LIBRARY:
                return TekItems.STRUCTURE_LIBRARY_TOKEN.get();
            default:
                return null;
        }
    }

    private static final class VanillaTemplate {
        private static final ResourceLocation TOWNHALL =
                vanilla("village/plains/houses/plains_big_house_1");
        private static final ResourceLocation STORAGE =
                vanilla("village/plains/houses/plains_tool_smith_1");
        private static final ResourceLocation HOME =
                vanilla("village/plains/houses/plains_medium_house_1");
        private static final ResourceLocation FARM =
                vanilla("village/plains/houses/plains_shepherds_house_1");
        private static final ResourceLocation MINESHAFT =
                vanilla("village/plains/houses/plains_masons_house_1");
        private static final ResourceLocation SCHOOL =
                vanilla("village/plains/houses/plains_library_1");
        private static final ResourceLocation TAVERN =
                vanilla("village/plains/houses/plains_butcher_shop_1");
        private static final ResourceLocation LIBRARY =
                vanilla("village/plains/houses/plains_library_2");

        private final ResourceLocation location;

        private VanillaTemplate(ResourceLocation location) {
            this.location = location;
        }

        private static VanillaTemplate forType(TekStructureType type) {
            switch (type) {
                case TOWNHALL:
                    return new VanillaTemplate(TOWNHALL);
                case STORAGE:
                    return new VanillaTemplate(STORAGE);
                case HOME:
                    return new VanillaTemplate(HOME);
                case FARM:
                    return new VanillaTemplate(FARM);
                case MINESHAFT:
                    return new VanillaTemplate(MINESHAFT);
                case SCHOOL:
                    return new VanillaTemplate(SCHOOL);
                case TAVERN:
                    return new VanillaTemplate(TAVERN);
                case LIBRARY:
                    return new VanillaTemplate(LIBRARY);
                default:
                    return new VanillaTemplate(HOME);
            }
        }

        private static ResourceLocation vanilla(String path) {
            return new ResourceLocation("minecraft", path);
        }
    }

    private static final class DoorSelection {
        private final BlockPos doorPos;
        private final BlockPos doorInside;
        private final Direction signFacing;
        private final int score;

        private DoorSelection(BlockPos doorPos, BlockPos doorInside, Direction signFacing, int score) {
            this.doorPos = doorPos;
            this.doorInside = doorInside;
            this.signFacing = signFacing;
            this.score = score;
        }

        private BlockPos framePos() {
            return this.doorPos.above(2).relative(this.signFacing).immutable();
        }
    }

    public static final class Result {
        private final TekStructureType type;
        private final BlockPos doorInside;
        private final BlockPos framePos;
        private final Direction signFacing;
        private final int placedBlocks;

        private Result(TekStructureType type, BlockPos doorInside, BlockPos framePos, Direction signFacing, int placedBlocks) {
            this.type = type;
            this.doorInside = doorInside;
            this.framePos = framePos;
            this.signFacing = signFacing;
            this.placedBlocks = placedBlocks;
        }

        private static Result failed(TekStructureType type, BlockPos pos, Direction signFacing) {
            return new Result(type, pos.immutable(), pos.immutable(), signFacing, 0);
        }

        public TekStructureType getType() {
            return this.type;
        }

        public BlockPos getDoorInside() {
            return this.doorInside;
        }

        public BlockPos getFramePos() {
            return this.framePos;
        }

        public Direction getSignFacing() {
            return this.signFacing;
        }

        public int getPlacedBlocks() {
            return this.placedBlocks;
        }
    }
}
