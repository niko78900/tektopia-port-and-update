/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockCrops
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.event.world.BlockEvent$CropGrowEvent
 */
package net.tangotek.tektopia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.tangotek.tektopia.Village;

public class VillageFarm {
    private static int nextId = 1;
    private final int farmId;
    private int cropUpdateTick = 0;
    protected final World world;
    private int distanceToVillageCenter = 0;
    protected AxisAlignedBB aabb;
    protected List<BlockPos> fullCrops = new ArrayList<BlockPos>();
    protected Set<BlockPos> activeFarmland = new HashSet<BlockPos>();
    private int blocks = 0;
    private double weightScore;
    public static int MAX_AREA = 1000;

    public VillageFarm(World w, BlockPos startPos, Village v) {
        this.farmId = nextId++;
        this.world = w;
        this.aabb = new AxisAlignedBB(startPos, startPos);
        HashSet<BlockPos> tested = new HashSet<BlockPos>();
        this.testFarmLand(startPos, tested);
        this.blocks = tested.size();
        this.distanceToVillageCenter = (int)this.getFarmCenter().func_72438_d(new Vec3d((double)v.getCenter().func_177958_n(), (double)v.getCenter().func_177956_o(), (double)v.getCenter().func_177952_p()));
        this.cropUpdateTick = 500 + this.world.field_73012_v.nextInt(500);
        this.updateWeightScore(v.getSize());
    }

    public int size() {
        return this.blocks;
    }

    public void updateWeightScore(int villageSize) {
        this.weightScore = (double)villageSize / (double)this.distanceToVillageCenter * (double)this.blocks;
    }

    public double getWeightScore() {
        return this.weightScore;
    }

    private Vec3d getFarmCenter() {
        return new Vec3d(this.aabb.field_72340_a + (this.aabb.field_72336_d - this.aabb.field_72340_a) * 0.5, this.aabb.field_72338_b + (this.aabb.field_72337_e - this.aabb.field_72338_b) * 0.5, this.aabb.field_72339_c + (this.aabb.field_72334_f - this.aabb.field_72339_c) * 0.5);
    }

    public boolean isBlockInside(BlockPos pos) {
        if ((double)pos.func_177958_n() >= this.aabb.field_72340_a && (double)pos.func_177958_n() <= this.aabb.field_72336_d && (double)pos.func_177952_p() >= this.aabb.field_72339_c && (double)pos.func_177952_p() <= this.aabb.field_72334_f) {
            return (double)pos.func_177956_o() >= this.aabb.field_72338_b && (double)pos.func_177956_o() <= this.aabb.field_72337_e;
        }
        return false;
    }

    public BlockPos getMaxAgeCrop() {
        while (!this.fullCrops.isEmpty()) {
            BlockPos pos = this.fullCrops.remove(this.world.field_73012_v.nextInt(this.fullCrops.size()));
            if (!VillageFarm.isMaxAgeCrop(this.world, pos)) continue;
            return pos;
        }
        return null;
    }

    public BlockPos getFarmland(Predicate<BlockPos> pred) {
        for (int i = 0; i < 20; ++i) {
            int z;
            int x = MathHelper.func_76136_a((Random)this.world.field_73012_v, (int)((int)this.aabb.field_72340_a), (int)((int)this.aabb.field_72336_d));
            BlockPos pos = new BlockPos((double)x, this.aabb.field_72338_b, (double)(z = MathHelper.func_76136_a((Random)this.world.field_73012_v, (int)((int)this.aabb.field_72339_c), (int)((int)this.aabb.field_72334_f))));
            if (!this.isNearWater(pos) || !this.isFarmlandAdjacent(pos) || !pred.test(pos) || this.activeFarmland.contains(pos)) continue;
            this.activeFarmland.add(pos);
            return pos;
        }
        return null;
    }

    private boolean isFarmlandAdjacent(BlockPos pos) {
        int count = 0;
        if (this.world.func_180495_p(pos.func_177976_e()).func_177230_c() == Blocks.field_150458_ak) {
            ++count;
        }
        if (this.world.func_180495_p(pos.func_177978_c()).func_177230_c() == Blocks.field_150458_ak) {
            ++count;
        }
        if (this.world.func_180495_p(pos.func_177968_d()).func_177230_c() == Blocks.field_150458_ak) {
            ++count;
        }
        if (this.world.func_180495_p(pos.func_177974_f()).func_177230_c() == Blocks.field_150458_ak) {
            ++count;
        }
        return count >= 2;
    }

    private boolean isNearWater(BlockPos cropPos) {
        for (BlockPos testPos : BlockPos.func_177980_a((BlockPos)cropPos.func_177982_a(-4, 0, -4), (BlockPos)cropPos.func_177982_a(4, 0, 4))) {
            Block testBlock = this.world.func_180495_p(testPos).func_177230_c();
            if (testBlock != Blocks.field_150355_j && testBlock != Blocks.field_150358_i) continue;
            return true;
        }
        return false;
    }

    public int distanceToVillageCenter() {
        return this.distanceToVillageCenter;
    }

    public void update(BlockPos villageCenter) {
        --this.cropUpdateTick;
        if (this.cropUpdateTick <= 0) {
            this.refreshFullCrops();
            this.activeFarmland.clear();
        }
    }

    private void testFarmLand(BlockPos pos, Set<BlockPos> tested) {
        if (VillageFarm.isFarmLand(this.world, pos) && this.getArea() < MAX_AREA && !tested.contains(pos)) {
            this.aabb = this.aabb.func_111270_a(new AxisAlignedBB(pos, pos));
            tested.add(pos);
            if (VillageFarm.isMaxAgeCrop(this.world, pos.func_177984_a())) {
                this.fullCrops.add(pos.func_177984_a());
            }
            this.testFarmLand(pos.func_177976_e(), tested);
            this.testFarmLand(pos.func_177974_f(), tested);
            this.testFarmLand(pos.func_177978_c(), tested);
            this.testFarmLand(pos.func_177968_d(), tested);
        }
    }

    public void onCropGrowEvent(BlockEvent.CropGrowEvent event) {
        if (this.isBlockInside(event.getPos().func_177977_b()) && VillageFarm.isMaxAgeCrop(this.world, event.getPos())) {
            this.fullCrops.add(event.getPos());
        }
    }

    private void refreshFullCrops() {
        this.fullCrops.clear();
        for (int x = (int)this.aabb.field_72340_a; x <= (int)this.aabb.field_72336_d; ++x) {
            for (int z = (int)this.aabb.field_72339_c; z <= (int)this.aabb.field_72334_f; ++z) {
                BlockPos pos = new BlockPos((double)x, this.aabb.field_72338_b, (double)z).func_177984_a();
                if (!VillageFarm.isMaxAgeCrop(this.world, pos)) continue;
                this.fullCrops.add(pos);
            }
        }
        this.cropUpdateTick = 250 + this.world.field_73012_v.nextInt(250);
    }

    public AxisAlignedBB getAABB() {
        return this.aabb;
    }

    public Vec3d getCenter() {
        return this.aabb.func_189972_c();
    }

    public String debug() {
        String result = "Farm #" + this.farmId + "  fullCrops: " + this.fullCrops.size() + " W: " + (int)this.getWeightScore() + "  C: " + new BlockPos(this.getCenter()) + " XZ: " + (this.aabb.field_72336_d - this.aabb.field_72340_a) + ", " + (this.aabb.field_72334_f - this.aabb.field_72339_c);
        return result;
    }

    public int getArea() {
        return (int)(this.aabb.field_72336_d - this.aabb.field_72340_a) * (int)(this.aabb.field_72334_f - this.aabb.field_72339_c);
    }

    public static boolean isFarmLand(World world, BlockPos pos) {
        Block block = world.func_180495_p(pos).func_177230_c();
        return block == Blocks.field_150458_ak;
    }

    public static boolean isMaxAgeCrop(World world, BlockPos pos) {
        IBlockState iblockstate = world.func_180495_p(pos);
        Block block = iblockstate.func_177230_c();
        return block instanceof BlockCrops && ((BlockCrops)block).func_185525_y(iblockstate);
    }
}

