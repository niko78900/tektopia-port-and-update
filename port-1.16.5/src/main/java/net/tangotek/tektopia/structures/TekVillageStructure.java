package net.tangotek.tektopia.structures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public abstract class TekVillageStructure {
    protected static final int MAX_FLOOR = 4096;
    protected static final int MAX_SCAN_HEIGHT = 30;
    protected static final int MIN_ROOM_HEIGHT = 2;

    protected final ServerWorld level;
    protected final TekStructureType type;
    protected final BlockPos doorInside;
    protected final Direction signFacing;
    protected final List<BlockPos> floorTiles = new ArrayList<>();
    protected final Map<Block, List<BlockPos>> specialBlocks = new HashMap<>();

    protected AxisAlignedBB bounds;
    protected BlockPos safeSpot;
    protected int ceilingHeightSum;
    protected boolean specialAdded;

    protected TekVillageStructure(ServerWorld level, TekStructureType type, BlockPos doorInside, Direction signFacing) {
        this.level = level;
        this.type = type;
        this.doorInside = doorInside.immutable();
        this.signFacing = signFacing;
    }

    public final void rescan() {
        this.specialBlocks.clear();
        this.safeSpot = null;
        this.bounds = new AxisAlignedBB(this.doorInside, this.doorInside.above(2));
        this.floorTiles.clear();
        this.ceilingHeightSum = 0;
        this.onFloorScanStart();
        this.scanFloor(this.doorInside);
        if (this.safeSpot == null) {
            this.safeSpot = this.doorInside;
        }
        this.onFloorScanEnd();
    }

    protected void onFloorScanStart() {
    }

    protected void onFloorScanEnd() {
    }

    protected void scanFloor(BlockPos startPos) {
        Deque<BlockPos> toScan = new ArrayDeque<>();
        Set<BlockPos> queued = new HashSet<>();
        toScan.add(startPos);
        queued.add(startPos);

        while (!toScan.isEmpty() && this.floorTiles.size() <= MAX_FLOOR) {
            BlockPos curPos = toScan.pollFirst();
            if (!this.tryAddFloorTile(curPos)) {
                continue;
            }
            this.queueFloorPos(toScan, queued, curPos.west());
            this.queueFloorPos(toScan, queued, curPos.north());
            this.queueFloorPos(toScan, queued, curPos.east());
            this.queueFloorPos(toScan, queued, curPos.south());

            this.queueVerticalCandidates(toScan, queued, curPos, curPos.west());
            this.queueVerticalCandidates(toScan, queued, curPos, curPos.north());
            this.queueVerticalCandidates(toScan, queued, curPos, curPos.east());
            this.queueVerticalCandidates(toScan, queued, curPos, curPos.south());
        }
    }

    private boolean tryAddFloorTile(BlockPos pos) {
        if (this.floorTiles.size() > MAX_FLOOR || this.floorTiles.contains(pos)) {
            return false;
        }
        int height = this.scanRoomHeight(pos);
        if (height < MIN_ROOM_HEIGHT || this.isPassable(pos.below())) {
            return false;
        }
        this.ceilingHeightSum += height;
        this.floorTiles.add(pos);
        this.bounds = this.bounds.minmax(new AxisAlignedBB(pos));
        if (this.safeSpot == null && this.level.noCollision(new AxisAlignedBB(pos))) {
            this.safeSpot = pos;
        }
        return true;
    }

    private void queueFloorPos(Deque<BlockPos> queue, Set<BlockPos> queued, BlockPos pos) {
        if (!queued.contains(pos) && !this.floorTiles.contains(pos)) {
            queue.addLast(pos);
            queued.add(pos);
        }
    }

    private void queueVerticalCandidates(Deque<BlockPos> queue, Set<BlockPos> queued, BlockPos from, BlockPos sidePos) {
        BlockPos upPos = sidePos.above();
        BlockPos downPos = sidePos.below();
        if (this.canTraverseVertical(from, sidePos, upPos)) {
            this.queueFloorPos(queue, queued, upPos);
        }
        if (this.canTraverseVertical(from, sidePos, downPos)) {
            this.queueFloorPos(queue, queued, downPos);
        }
    }

    private boolean canTraverseVertical(BlockPos from, BlockPos sidePos, BlockPos to) {
        int dy = to.getY() - from.getY();
        if (dy != 1 && dy != -1) {
            return false;
        }
        if (this.isClimbable(from) || this.isClimbable(sidePos) || this.isClimbable(to)) {
            return true;
        }

        BlockState sideState = this.level.getBlockState(sidePos);
        if (!this.isPassable(sidePos) && !this.isDoorOrGate(sideState.getBlock())) {
            return false;
        }

        Block fromFloor = this.level.getBlockState(from.below()).getBlock();
        Block sideFloor = this.level.getBlockState(sidePos.below()).getBlock();
        Block toFloor = this.level.getBlockState(to.below()).getBlock();
        return this.isStepConnector(fromFloor) || this.isStepConnector(sideFloor) || this.isStepConnector(toFloor);
    }

    protected int scanRoomHeight(BlockPos pos) {
        for (int i = 0; i < MAX_SCAN_HEIGHT; i++) {
            BlockPos p = pos.above(i);
            BlockState state = this.level.getBlockState(p);
            Block block = state.getBlock();
            this.specialAdded = false;
            if (i == 0) {
                this.scanSpecialBlock(p, block);
            }
            if (this.specialAdded) {
                continue;
            }
            if (this.isPassable(p) && !this.isDoorOrGate(block)) {
                continue;
            }
            return i;
        }
        return 0;
    }

    protected void scanSpecialBlock(BlockPos pos, Block block) {
        // subclasses provide structure-specific specials
    }

    protected void addSpecialBlock(Block block, BlockPos pos) {
        List<BlockPos> list = this.specialBlocks.computeIfAbsent(block, ignored -> new ArrayList<>());
        this.specialAdded = true;
        if (!list.contains(pos)) {
            list.add(pos.immutable());
        }
    }

    protected boolean isPassable(BlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        return state.getCollisionShape(this.level, pos).isEmpty();
    }

    private boolean isDoorOrGate(Block block) {
        return block instanceof DoorBlock || block instanceof FenceGateBlock;
    }

    private boolean isClimbable(BlockPos pos) {
        Block block = this.level.getBlockState(pos).getBlock();
        return block instanceof LadderBlock || block instanceof VineBlock;
    }

    private boolean isStepConnector(Block block) {
        return block instanceof StairsBlock || block instanceof SlabBlock;
    }

    public TekStructureType getType() {
        return this.type;
    }

    public BlockPos getDoorInside() {
        return this.doorInside;
    }

    public BlockPos getSafeSpot() {
        return this.safeSpot;
    }

    public AxisAlignedBB getBounds() {
        return this.bounds;
    }

    public int getFloorTileCount() {
        return this.floorTiles.size();
    }

    public double getAverageCeilingHeight() {
        if (this.floorTiles.isEmpty()) {
            return 0.0D;
        }
        return (double) this.ceilingHeightSum / (double) this.floorTiles.size();
    }

    public BlockPos getRandomFloorTile(Random random) {
        if (this.floorTiles.isEmpty()) {
            return null;
        }
        return this.floorTiles.get(random.nextInt(this.floorTiles.size()));
    }
}
