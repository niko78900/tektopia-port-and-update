package net.tangotek.tektopia.village;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.structures.TekStructureType;

public final class TekStructureDiscovery {
    private TekStructureDiscovery() {
    }

    public static List<DiscoveredStructure> discoverFromFrames(ServerWorld level, BlockPos center, int radius) {
        if (radius < 1) {
            return Collections.emptyList();
        }
        AxisAlignedBB scanBounds = new AxisAlignedBB(center).inflate(radius);
        List<ItemFrameEntity> frames = level.getEntitiesOfClass(
                ItemFrameEntity.class,
                scanBounds,
                frame -> frame != null && frame.isAlive()
        );
        if (frames.isEmpty()) {
            return Collections.emptyList();
        }

        List<DiscoveredStructure> discovered = new ArrayList<>();
        for (ItemFrameEntity frame : frames) {
            TekStructureType structureType = TekStructureType.fromFrameItem(frame.getItem());
            if (structureType == null) {
                continue;
            }
            Direction signFacing = frame.getDirection();
            if (signFacing == null) {
                signFacing = Direction.NORTH;
            }
            BlockPos framePos = frame.getPos().immutable();
            BlockPos doorPos = findDoor(level, framePos, signFacing);
            BlockPos doorInside = doorPos == null
                    ? framePos.relative(signFacing.getOpposite()).immutable()
                    : doorPos.relative(signFacing.getOpposite()).immutable();
            discovered.add(new DiscoveredStructure(
                    structureType,
                    frame,
                    frame.getUUID(),
                    frame.getItem().copy(),
                    framePos,
                    doorPos,
                    doorInside,
                    signFacing
            ));
        }
        return discovered;
    }

    @Nullable
    private static BlockPos findDoor(ServerWorld level, BlockPos framePos, Direction signFacing) {
        if (!signFacing.getAxis().isHorizontal()) {
            return null;
        }

        BlockPos behindFrame = framePos.relative(signFacing.getOpposite());
        BlockPos candidate = behindFrame.relative(signFacing.getClockWise());
        if (!isDoorOrGate(level, candidate)) {
            candidate = behindFrame.relative(signFacing.getCounterClockWise());
        }
        if (!isDoorOrGate(level, candidate)) {
            candidate = behindFrame.below(2);
        }
        if (!isDoorOrGate(level, candidate)) {
            return null;
        }
        if (isDoorOrGate(level, candidate.below())) {
            candidate = candidate.below();
        }
        return candidate.immutable();
    }

    private static boolean isDoorOrGate(ServerWorld level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        return block instanceof DoorBlock || block instanceof FenceGateBlock;
    }

    public static final class DiscoveredStructure {
        private final TekStructureType type;
        private final ItemFrameEntity frame;
        private final UUID frameId;
        private final ItemStack markerItem;
        private final BlockPos framePos;
        private final BlockPos doorPos;
        private final BlockPos doorInside;
        private final Direction signFacing;

        public DiscoveredStructure(TekStructureType type, ItemFrameEntity frame, UUID frameId, ItemStack markerItem, BlockPos framePos, BlockPos doorPos, BlockPos doorInside, Direction signFacing) {
            this.type = type;
            this.frame = frame;
            this.frameId = frameId;
            this.markerItem = markerItem == null ? ItemStack.EMPTY : markerItem.copy();
            this.framePos = framePos;
            this.doorPos = doorPos == null ? null : doorPos.immutable();
            this.doorInside = doorInside;
            this.signFacing = signFacing;
        }

        public TekStructureType getType() {
            return this.type;
        }

        public UUID getFrameId() {
            return this.frameId;
        }

        public ItemFrameEntity getFrame() {
            return this.frame;
        }

        public ItemStack getMarkerItem() {
            return this.markerItem.copy();
        }

        public BlockPos getFramePos() {
            return this.framePos;
        }

        @Nullable
        public BlockPos getDoorPos() {
            return this.doorPos;
        }

        public BlockPos getDoorInside() {
            return this.doorInside;
        }

        public Direction getSignFacing() {
            return this.signFacing;
        }
    }
}
