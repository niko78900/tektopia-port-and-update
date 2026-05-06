package net.tangotek.tektopia.worldgen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.registry.TekItems;
import net.tangotek.tektopia.structures.TekStructureType;

public final class TekStarterStructureGenerator {
    private static final int HALF_SIZE = 3;

    private TekStarterStructureGenerator() {
    }

    public static boolean isStarterType(TekStructureType type) {
        return type == TekStructureType.TOWNHALL
                || type == TekStructureType.STORAGE
                || type == TekStructureType.HOME
                || type == TekStructureType.FARM
                || type == TekStructureType.MINESHAFT;
    }

    public static Result generate(ServerWorld level, TekStructureType type, BlockPos near, Direction facing) {
        Direction horizontalFacing = facing.getAxis().isHorizontal() ? facing : Direction.NORTH;
        BlockPos surface = level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, near);
        BlockPos center = surface.immutable();
        BlockPos doorInside = center.relative(horizontalFacing, HALF_SIZE - 1).immutable();
        BlockPos framePos = doorInside.relative(horizontalFacing).immutable();

        int placed = buildShell(level, center);
        placed += placeSpecials(level, type, center, doorInside, horizontalFacing);
        if (placeMarkerFrame(level, type, framePos, horizontalFacing)) {
            placed++;
        }

        return new Result(type, doorInside, framePos, horizontalFacing, placed);
    }

    private static int buildShell(ServerWorld level, BlockPos center) {
        int placed = 0;
        for (int dx = -HALF_SIZE; dx <= HALF_SIZE; dx++) {
            for (int dz = -HALF_SIZE; dz <= HALF_SIZE; dz++) {
                BlockPos floor = center.offset(dx, -1, dz);
                BlockPos ceiling = center.offset(dx, 3, dz);
                placed += setBlock(level, floor, Blocks.OAK_PLANKS.defaultBlockState());
                placed += setBlock(level, ceiling, Blocks.OAK_PLANKS.defaultBlockState());

                boolean wall = Math.abs(dx) == HALF_SIZE || Math.abs(dz) == HALF_SIZE;
                for (int dy = 0; dy <= 2; dy++) {
                    BlockPos body = center.offset(dx, dy, dz);
                    placed += setBlock(level, body, wall ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.AIR.defaultBlockState());
                }
            }
        }
        return placed;
    }

    private static int placeSpecials(ServerWorld level, TekStructureType type, BlockPos center, BlockPos doorInside, Direction facing) {
        switch (type) {
            case STORAGE:
                return setBlock(level, center, Blocks.CHEST.defaultBlockState())
                        + setBlock(level, center.east(), Blocks.CRAFTING_TABLE.defaultBlockState());
            case HOME:
                return setBlock(level, center, Blocks.WHITE_BED.defaultBlockState())
                        + setBlock(level, center.west(), Blocks.CHEST.defaultBlockState());
            case FARM:
                return setBlock(level, center, Blocks.COMPOSTER.defaultBlockState())
                        + setBlock(level, center.east(), Blocks.HAY_BLOCK.defaultBlockState())
                        + setBlock(level, center.west(), Blocks.CRAFTING_TABLE.defaultBlockState());
            case MINESHAFT:
                BlockPos opening = doorInside.relative(facing.getOpposite());
                return setBlock(level, center, Blocks.STONECUTTER.defaultBlockState())
                        + setBlock(level, opening.below(), Blocks.AIR.defaultBlockState())
                        + setBlock(level, opening.below(2), Blocks.AIR.defaultBlockState());
            case TOWNHALL:
            default:
                return setBlock(level, center, Blocks.CRAFTING_TABLE.defaultBlockState());
        }
    }

    private static boolean placeMarkerFrame(ServerWorld level, TekStructureType type, BlockPos framePos, Direction facing) {
        Item token = tokenFor(type);
        if (token == null) {
            return false;
        }

        ItemFrameEntity frame = new ItemFrameEntity(level, framePos, facing);
        frame.setItem(new ItemStack(token));
        return level.addFreshEntity(frame);
    }

    private static int setBlock(ServerWorld level, BlockPos pos, BlockState state) {
        return level.setBlock(pos, state, 3) ? 1 : 0;
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
            default:
                return null;
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
