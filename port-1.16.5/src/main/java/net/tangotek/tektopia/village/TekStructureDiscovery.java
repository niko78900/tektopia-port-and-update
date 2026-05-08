package net.tangotek.tektopia.village;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
            BlockPos framePos = frame.blockPosition().immutable();
            BlockPos doorInside = framePos.relative(signFacing.getOpposite()).immutable();
            discovered.add(new DiscoveredStructure(
                    structureType,
                    frame.getUUID(),
                    frame.getItem().copy(),
                    framePos,
                    doorInside,
                    signFacing
            ));
        }
        return discovered;
    }

    public static final class DiscoveredStructure {
        private final TekStructureType type;
        private final UUID frameId;
        private final ItemStack markerItem;
        private final BlockPos framePos;
        private final BlockPos doorInside;
        private final Direction signFacing;

        public DiscoveredStructure(TekStructureType type, UUID frameId, ItemStack markerItem, BlockPos framePos, BlockPos doorInside, Direction signFacing) {
            this.type = type;
            this.frameId = frameId;
            this.markerItem = markerItem == null ? ItemStack.EMPTY : markerItem.copy();
            this.framePos = framePos;
            this.doorInside = doorInside;
            this.signFacing = signFacing;
        }

        public TekStructureType getType() {
            return this.type;
        }

        public UUID getFrameId() {
            return this.frameId;
        }

        public ItemStack getMarkerItem() {
            return this.markerItem.copy();
        }

        public BlockPos getFramePos() {
            return this.framePos;
        }

        public BlockPos getDoorInside() {
            return this.doorInside;
        }

        public Direction getSignFacing() {
            return this.signFacing;
        }
    }
}
