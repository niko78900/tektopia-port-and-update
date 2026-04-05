/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.BlockFenceGate
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.tangotek.tektopia.ModBlocks;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.caps.VillageDataProvider;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;
import net.tangotek.tektopia.tickjob.TickJobQueue;

public abstract class VillageStructure {
    protected BlockPos door;
    protected BlockPos framePos;
    protected EnumFacing signFacing;
    protected Village village;
    protected final int signEntityId;
    public final VillageStructureType type;
    protected final World world;
    protected List<BlockPos> floorTiles = new ArrayList<BlockPos>();
    protected int ceilingHeightSum = 0;
    protected boolean isValid = true;
    protected AxisAlignedBB aabb;
    protected Map<Block, List<BlockPos>> specialBlocks = new HashMap<Block, List<BlockPos>>();
    protected static int MAX_FLOOR = 500;
    private static int MIN_FLOOR = 4;
    protected boolean specialAdded = false;
    protected TickJobQueue jobs = new TickJobQueue();
    protected BlockPos safeSpot = null;
    private Set<BlockPos> occupiedSpecials = new HashSet<BlockPos>();

    protected VillageStructure(World world, Village v, EntityItemFrame itemFrame, VillageStructureType t, String name) {
        this.type = t;
        this.world = world;
        this.village = v;
        this.framePos = itemFrame.func_174857_n();
        this.signFacing = itemFrame.field_174860_b;
        this.signEntityId = itemFrame.func_145782_y();
    }

    public void setup() {
        this.door = this.findDoor();
        if (this.door != null) {
            this.doFloorScan();
        }
        this.setupServerJobs();
        this.validate();
    }

    public Village getVillage() {
        return this.village;
    }

    public int getMaxAllowed() {
        return 0;
    }

    public void addJob(TickJob job) {
        if (this.world.field_72995_K) {
            throw new IllegalStateException("Cannot add tick jobs on client");
        }
        this.jobs.addJob(job);
    }

    protected void setupServerJobs() {
        this.addJob(new TickJob(50, 100, true, () -> this.validate()));
        this.addJob(new TickJob(180, 120, true, () -> {
            if (this.isValid) {
                this.doFloorScan();
            }
        }));
    }

    public void update() {
        this.jobs.tick();
    }

    protected void onFloorScanStart() {
    }

    protected void onFloorScanEnd() {
    }

    public BlockPos getDoorOutside() {
        return this.getDoorOutside(1);
    }

    public BlockPos getDoorOutside(int dist) {
        return this.door.func_177967_a(this.signFacing, dist);
    }

    public BlockPos getDoorInside() {
        return this.door.func_177967_a(this.signFacing, -1);
    }

    protected void doFloorScan() {
        this.specialBlocks.clear();
        this.safeSpot = null;
        this.aabb = new AxisAlignedBB(this.door, this.door.func_177981_b(2));
        this.floorTiles.clear();
        this.ceilingHeightSum = 0;
        this.onFloorScanStart();
        this.scanFloor(this.getDoorInside());
        if (this.safeSpot == null) {
            this.safeSpot = this.getDoorInside();
        }
        this.onFloorScanEnd();
    }

    protected void scanFloor(BlockPos pos) {
        Deque<BlockPos> toScan = new ArrayDeque<BlockPos>();
        Set<BlockPos> queued = new HashSet<BlockPos>();
        toScan.add(pos);
        queued.add(pos);
        while (!toScan.isEmpty() && this.floorTiles.size() <= MAX_FLOOR) {
            BlockPos curPos = (BlockPos)toScan.pollFirst();
            if (!this.tryAddFloorTile(curPos)) continue;
            this.queueFloorPos(toScan, queued, curPos.func_177976_e());
            this.queueFloorPos(toScan, queued, curPos.func_177978_c());
            this.queueFloorPos(toScan, queued, curPos.func_177974_f());
            this.queueFloorPos(toScan, queued, curPos.func_177968_d());
            this.queueVerticalCandidates(toScan, queued, curPos, curPos.func_177976_e());
            this.queueVerticalCandidates(toScan, queued, curPos, curPos.func_177978_c());
            this.queueVerticalCandidates(toScan, queued, curPos, curPos.func_177974_f());
            this.queueVerticalCandidates(toScan, queued, curPos, curPos.func_177968_d());
        }
    }

    private boolean tryAddFloorTile(BlockPos pos) {
        int height;
        if (this.floorTiles.size() > MAX_FLOOR || this.floorTiles.contains(pos) || (height = this.scanRoomHeight(pos)) < 2 || BasePathingNode.isPassable(this.world, pos.func_177977_b())) {
            return false;
        }
        this.ceilingHeightSum += height;
        this.floorTiles.add(pos);
        AxisAlignedBB bbox = new AxisAlignedBB(pos);
        this.aabb = this.aabb.func_111270_a(bbox);
        if (this.safeSpot == null && !this.world.func_184143_b(new AxisAlignedBB((double)(pos.func_177958_n() - 1), (double)pos.func_177956_o(), (double)(pos.func_177952_p() - 1), (double)(pos.func_177958_n() + 1), (double)(pos.func_177956_o() + 1), (double)(pos.func_177952_p() + 1)))) {
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
        BlockPos upPos = sidePos.func_177984_a();
        BlockPos downPos = sidePos.func_177977_b();
        if (this.canTraverseVertical(from, sidePos, upPos)) {
            this.queueFloorPos(queue, queued, upPos);
        }
        if (this.canTraverseVertical(from, sidePos, downPos)) {
            this.queueFloorPos(queue, queued, downPos);
        }
    }

    private boolean canTraverseVertical(BlockPos from, BlockPos sidePos, BlockPos to) {
        int dy = to.func_177956_o() - from.func_177956_o();
        if (dy != 1 && dy != -1) {
            return false;
        }
        if (this.isClimbable(from) || this.isClimbable(sidePos) || this.isClimbable(to)) {
            return true;
        }
        if (!BasePathingNode.isPassable(this.world, sidePos) && !VillageStructure.isWoodDoor(this.world, sidePos) && !VillageStructure.isGate(this.world, sidePos)) {
            return false;
        }
        Block fromFloor = this.world.func_180495_p(from.func_177977_b()).func_177230_c();
        Block sideFloor = this.world.func_180495_p(sidePos.func_177977_b()).func_177230_c();
        Block toFloor = this.world.func_180495_p(to.func_177977_b()).func_177230_c();
        return this.isStepConnector(fromFloor) || this.isStepConnector(sideFloor) || this.isStepConnector(toFloor);
    }

    private boolean isStepConnector(Block block) {
        return block instanceof BlockStairs || block instanceof BlockSlab;
    }

    private boolean isClimbable(BlockPos pos) {
        Block b = this.world.func_180495_p(pos).func_177230_c();
        return b instanceof BlockLadder || b instanceof BlockVine;
    }

    protected int scanRoomHeight(BlockPos pos) {
        for (int i = 0; i < 30; ++i) {
            BlockPos p = pos.func_177981_b(i);
            Block b = this.world.func_180495_p(p).func_177230_c();
            this.specialAdded = false;
            if (i == 0) {
                this.scanSpecialBlock(p, b);
            }
            if (this.specialAdded || BasePathingNode.isPassable(this.world, p) && !VillageStructure.isWoodDoor(this.world, pos) && !VillageStructure.isGate(this.world, pos)) continue;
            return i;
        }
        return 0;
    }

    public BlockPos getSafeSpot() {
        return this.safeSpot;
    }

    protected void scanSpecialBlock(BlockPos pos, Block block) {
        if (block == ModBlocks.blockChair) {
            this.addSpecialBlock((Block)ModBlocks.blockChair, pos);
        }
    }

    protected void addSpecialBlock(Block block, BlockPos bp) {
        List<BlockPos> list = this.specialBlocks.get(block);
        if (list == null) {
            list = new ArrayList<BlockPos>();
            this.specialBlocks.put(block, list);
        }
        this.specialAdded = true;
        if (!list.contains(bp)) {
            list.add(bp);
        }
    }

    public BlockPos getUnoccupiedSpecialBlock(Block block) {
        List<BlockPos> list = this.specialBlocks.get(block);
        if (list != null) {
            Collections.shuffle(list);
            return list.stream().filter(b -> !this.isSpecialBlockOccupied((BlockPos)b)).findAny().orElse(null);
        }
        return null;
    }

    public List<BlockPos> getSpecialBlocks(Block block) {
        List<BlockPos> list = this.specialBlocks.get(block);
        if (list != null && !list.isEmpty()) {
            return list;
        }
        return new ArrayList<BlockPos>();
    }

    public boolean vacateSpecialBlock(BlockPos bp) {
        return this.occupiedSpecials.remove(bp);
    }

    public boolean occupySpecialBlock(BlockPos bp) {
        return this.occupiedSpecials.add(bp);
    }

    public boolean isSpecialBlockOccupied(BlockPos bp) {
        return this.occupiedSpecials.contains(bp);
    }

    protected boolean shouldVillagerSit(EntityVillagerTek villager) {
        return false;
    }

    public BlockPos tryVillagerSit(EntityVillagerTek villager) {
        List<BlockPos> chairs;
        BlockPos result = null;
        if (this.shouldVillagerSit(villager) && !(chairs = this.getSpecialBlocks((Block)ModBlocks.blockChair)).isEmpty()) {
            BlockPos takenChair;
            Collections.shuffle(chairs);
            Stream<BlockPos> availableChairs = chairs.stream().filter(c -> !this.isSpecialBlockOccupied((BlockPos)c));
            result = villager.func_70681_au().nextInt(3) == 0 ? (BlockPos)availableChairs.findAny().orElse(null) : ((takenChair = (BlockPos)chairs.stream().filter(c -> this.isSpecialBlockOccupied((BlockPos)c)).findAny().orElse(null)) == null ? (BlockPos)availableChairs.findAny().orElse(null) : (BlockPos)availableChairs.min(Comparator.comparing(bp -> bp.func_177951_i((Vec3i)takenChair))).orElse(null));
        }
        if (result != null) {
            this.occupySpecialBlock(result);
        }
        return result;
    }

    public boolean isStructureOverlapped(VillageStructure other) {
        return this.getAABB().func_72326_a(other.getAABB()) && this.floorTiles.stream().anyMatch(f -> other.floorTiles.contains(f));
    }

    public int getSitTime(EntityVillagerTek villager) {
        return 0;
    }

    protected BlockPos findDoor() {
        BlockPos dp = null;
        dp = this.framePos.func_177967_a(this.signFacing, -1).func_177967_a(this.signFacing.func_176746_e(), 1);
        if (!(VillageStructure.isWoodDoor(this.world, dp) || VillageStructure.isWoodDoor(this.world, dp = this.framePos.func_177967_a(this.signFacing, -1).func_177967_a(this.signFacing.func_176746_e(), -1)) || VillageStructure.isWoodDoor(this.world, dp = this.framePos.func_177967_a(this.signFacing, -1).func_177979_c(2)))) {
            dp = null;
        }
        if (dp != null && VillageStructure.isWoodDoor(this.world, dp.func_177977_b())) {
            dp = dp.func_177977_b();
        }
        return dp;
    }

    public static boolean isWoodDoor(World world, BlockPos pos) {
        if (pos == null) {
            return false;
        }
        IBlockState iblockstate = world.func_180495_p(pos);
        Block block = iblockstate.func_177230_c();
        if (block instanceof BlockDoor) {
            return iblockstate.func_185904_a() == Material.field_151575_d;
        }
        return false;
    }

    public static boolean isGate(World world, BlockPos pos) {
        if (pos == null) {
            return false;
        }
        IBlockState iblockstate = world.func_180495_p(pos);
        Block block = iblockstate.func_177230_c();
        return block instanceof BlockFenceGate;
    }

    public boolean isBlockInside(BlockPos pos) {
        if (this.aabb.func_72318_a(new Vec3d((double)pos.func_177958_n() + 0.5, (double)pos.func_177956_o() + 0.5, (double)pos.func_177952_p() + 0.5))) {
            for (BlockPos p : this.floorTiles) {
                if (!p.equals((Object)pos)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean isBlockNear(BlockPos pos, double dist) {
        if (this.aabb.func_72314_b(dist, dist, dist).func_72318_a(new Vec3d((double)pos.func_177958_n() + 0.5, (double)pos.func_177956_o() + 0.5, (double)pos.func_177952_p() + 0.5))) {
            double distSq = dist * dist;
            for (BlockPos p : this.floorTiles) {
                if (!(p.func_177951_i((Vec3i)pos) < distSq)) continue;
                return true;
            }
        }
        return false;
    }

    public List<EntityPlayer> getPlayersInside() {
        List players = this.world.func_175661_b(EntityPlayer.class, p -> this.aabb.func_72318_a(p.func_174791_d()));
        return players;
    }

    public BlockPos getRandomFloorTile() {
        if (this.floorTiles.isEmpty()) {
            return null;
        }
        int index = this.world.field_73012_v.nextInt(this.floorTiles.size());
        BlockPos bp = this.floorTiles.get(index);
        if (BasePathingNode.isPassable(this.world, bp)) {
            return bp;
        }
        return null;
    }

    public AxisAlignedBB getAABB() {
        return this.aabb;
    }

    public BlockPos getDoor() {
        return this.door;
    }

    public BlockPos getFramePos() {
        return this.framePos;
    }

    public void onDestroy() {
    }

    public float getCrowdedFactor() {
        int villagersInside;
        int densityRatio;
        int floorCount = this.floorTiles.size();
        float avgCeiling = (float)this.ceilingHeightSum / (float)floorCount;
        float modifier = 1.0f;
        if (this.type.tilesPerVillager > 0 && (densityRatio = floorCount / (villagersInside = this.getEntitiesInside(EntityVillagerTek.class).size())) < this.type.tilesPerVillager) {
            float compare = (float)densityRatio / (float)this.type.tilesPerVillager;
            modifier *= (compare - 0.5f) * 2.0f;
        }
        if ((double)avgCeiling < 2.5) {
            modifier *= 0.5f;
        }
        return 1.0f - modifier;
    }

    public EntityItemFrame getItemFrame() {
        Entity e = this.world.func_73045_a(this.signEntityId);
        if (e instanceof EntityItemFrame) {
            return (EntityItemFrame)e;
        }
        return null;
    }

    public IVillageData getData() {
        EntityItemFrame frame = this.getItemFrame();
        if (frame != null && frame.func_82335_i() != null) {
            IVillageData vd = (IVillageData)frame.func_82335_i().getCapability(VillageDataProvider.VILLAGE_DATA_CAPABILITY, null);
            return vd;
        }
        return null;
    }

    public <T extends Entity> List<T> getEntitiesInside(Class<? extends T> clazz) {
        List entList = this.world.func_72872_a(clazz, this.getAABB().func_72314_b(0.5, 3.0, 0.5));
        ListIterator itr = entList.listIterator();
        while (itr.hasNext()) {
            Entity ent = (Entity)itr.next();
            if (this.isBlockNear(ent.func_180425_c(), 2.0)) continue;
            itr.remove();
        }
        return entList;
    }

    public void debugOut(String text) {
        if (this.village != null) {
            this.village.debugOut("[" + this.type.name() + "] " + text);
        } else {
            System.out.println("[No Village] " + text);
        }
    }

    public boolean isValid() {
        return this.isValid;
    }

    public boolean validate() {
        this.isValid = true;
        if (this.door == null) {
            this.isValid = false;
        } else if (this.world.func_175667_e(this.door)) {
            if (!VillageStructure.isWoodDoor(this.world, this.door) && !VillageStructure.isGate(this.world, this.door)) {
                this.debugOut("Village struct is missing its door " + this.getFramePos());
                this.isValid = false;
            }
            if (this.isValid && this.floorTiles.size() > MAX_FLOOR) {
                this.debugOut("Village struct has too many floor tiles " + this.getFramePos());
                this.isValid = false;
            }
            Entity e = this.world.func_73045_a(this.signEntityId);
            if (this.isValid && (e == null || !(e instanceof EntityItemFrame))) {
                this.debugOut("Village struct frame is missing or wrong type | " + this.getFramePos());
                this.isValid = false;
            }
            EntityItemFrame itemFrame = (EntityItemFrame)e;
            if (this.isValid && itemFrame.func_174857_n() != this.framePos) {
                this.debugOut("Village struct center has moved " + this.getFramePos());
                this.isValid = false;
            }
            if (this.isValid && !this.type.isItemEqual(itemFrame.func_82335_i())) {
                this.debugOut("Village struct frame item has changed " + this.getFramePos());
                this.isValid = false;
            }
            if (this.isValid && this.floorTiles.size() < MIN_FLOOR) {
                this.isValid = false;
            }
        }
        return this.isValid;
    }

    public void checkOccupiedBlocks() {
        Iterator<BlockPos> itr = this.occupiedSpecials.iterator();
        while (itr.hasNext()) {
            BlockPos bp = itr.next();
            List villagers = this.world.func_72872_a(EntityVillagerTek.class, new AxisAlignedBB(bp).func_186662_g(1.0));
            if (!villagers.isEmpty()) continue;
            System.out.println("UnOccupying block " + bp + ". No villagers nearby");
            itr.remove();
        }
    }

    public boolean adjustsVillageCenter() {
        return true;
    }
}
