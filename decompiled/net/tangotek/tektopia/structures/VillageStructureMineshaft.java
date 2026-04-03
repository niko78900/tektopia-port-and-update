/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityDruid;
import net.tangotek.tektopia.entities.EntityMiner;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;

public class VillageStructureMineshaft
extends VillageStructure {
    private EntityVillagerTek currentMiner = null;
    private EntityDruid druid = null;
    private BlockPos miningPos;

    protected VillageStructureMineshaft(World world, Village v, EntityItemFrame itemFrame) {
        super(world, v, itemFrame, VillageStructureType.MINESHAFT, "Mineshaft");
    }

    public int getTunnelLength() {
        return this.floorTiles.size();
    }

    public BlockPos getMiningPos() {
        return this.miningPos;
    }

    public BlockPos getWalkingPos() {
        return this.miningPos.func_177967_a(this.signFacing, 1);
    }

    @Override
    public boolean adjustsVillageCenter() {
        return false;
    }

    @Override
    protected void setupServerJobs() {
        this.addJob(new TickJob(20, 10, true, () -> this.updateOccupants()));
        super.setupServerJobs();
    }

    public void updateOccupants() {
        List occupants = this.world.func_72872_a(EntityVillagerTek.class, this.aabb);
        if (occupants.isEmpty()) {
            this.setTunnelMiner(null);
        } else if (this.getTunnelMiner() == null) {
            this.setTunnelMiner((EntityVillagerTek)((Object)occupants.get(0)));
        } else if (!occupants.contains((Object)this.getTunnelMiner())) {
            this.setTunnelMiner(null);
        }
        EntityVillagerTek miner = this.getTunnelMiner();
        if (miner != null) {
            this.tryPlaceTorch(miner);
        }
    }

    public void tryPlaceTorch(EntityVillagerTek miner) {
        BlockPos pos = miner.func_180425_c();
        if (miner.field_70170_p.func_175671_l(pos) < 8 && !miner.getInventory().removeItems(EntityMiner.isTorch(), 1).isEmpty()) {
            miner.field_70170_p.func_175656_a(pos, Blocks.field_150478_aa.func_176223_P());
        }
    }

    public EntityVillagerTek getTunnelMiner() {
        return this.currentMiner;
    }

    private void setTunnelMiner(EntityVillagerTek miner) {
        this.currentMiner = miner;
    }

    public void setDruid(EntityDruid d) {
        this.druid = d;
    }

    public EntityDruid getDruid() {
        return this.druid;
    }

    public void checkExtendTunnel() {
        while (VillageStructureMineshaft.isTunnel(this, this.miningPos)) {
            this.addFloorTile(this.miningPos);
            this.miningPos = this.miningPos.func_177967_a(this.signFacing, -1);
        }
    }

    public void regrow(EntityVillagerTek villager) {
        if (this.getTunnelLength() > 3) {
            BlockPos regrowPos = this.getWalkingPos();
            this.regrowColumn(villager, regrowPos);
            this.regrowColumn(villager, regrowPos.func_177967_a(this.signFacing.func_176735_f(), 1));
            this.regrowColumn(villager, regrowPos.func_177967_a(this.signFacing.func_176746_e(), 1));
        }
        this.doFloorScan();
    }

    protected void regrowColumn(EntityVillagerTek villager, BlockPos pos) {
        this.regrowBlock(villager, pos.func_177977_b());
        this.regrowBlock(villager, pos);
        this.regrowBlock(villager, pos.func_177984_a());
        this.regrowBlock(villager, pos.func_177981_b(2));
    }

    protected void regrowBlock(EntityVillagerTek villager, BlockPos pos) {
        if (!VillageStructureMineshaft.isOre(villager.field_70170_p, pos)) {
            if (villager.func_70681_au().nextInt(1000) < villager.getSkillLerp(ProfessionType.DRUID, 25, 40)) {
                this.world.func_180501_a(pos, VillageStructureMineshaft.getRegrowBlock(villager).func_176223_P(), 2);
            } else {
                this.world.func_180501_a(pos, Blocks.field_150348_b.func_176223_P(), 2);
            }
        }
    }

    public static Block getRegrowBlock(EntityVillagerTek villager) {
        int oreRoll = villager.func_70681_au().nextInt(325);
        if (oreRoll < 10) {
            return Blocks.field_150482_ag;
        }
        if (oreRoll < 20) {
            return Blocks.field_150369_x;
        }
        if (oreRoll < 35) {
            return Blocks.field_150352_o;
        }
        if (oreRoll < 100) {
            return Blocks.field_150366_p;
        }
        if (oreRoll < 200) {
            return Blocks.field_150365_q;
        }
        if (villager.func_70681_au().nextInt(3) == 0) {
            return Blocks.field_150450_ax;
        }
        return Blocks.field_150348_b;
    }

    @Override
    protected void onFloorScanStart() {
        this.miningPos = this.door.func_177967_a(this.signFacing, -1);
        super.onFloorScanStart();
    }

    public BlockPos findOre(BlockPos pos, int distance, EnumFacing fromDir) {
        if (distance > 3) {
            return null;
        }
        if (this.world.func_175623_d(pos)) {
            BlockPos next = null;
            if (fromDir != EnumFacing.DOWN && (next = this.findOre(pos.func_177984_a(), distance + 1, EnumFacing.UP)) != null) {
                return next;
            }
            if (fromDir != EnumFacing.UP && (next = this.findOre(pos.func_177977_b(), distance + 1, EnumFacing.DOWN)) != null) {
                return next;
            }
            if (fromDir != this.signFacing.func_176746_e() && (next = this.findOre(pos.func_177967_a(this.signFacing.func_176735_f(), 1), distance + 1, this.signFacing.func_176735_f())) != null) {
                return next;
            }
            if (fromDir != this.signFacing.func_176735_f() && (next = this.findOre(pos.func_177967_a(this.signFacing.func_176746_e(), 1), distance + 1, this.signFacing.func_176746_e())) != null) {
                return next;
            }
        } else if (VillageStructureMineshaft.isOre(this.world, pos) && VillageStructureMineshaft.canDig(this.world, pos)) {
            return pos;
        }
        return null;
    }

    public static boolean isOre(World world, BlockPos pos) {
        Block b = world.func_180495_p(pos).func_177230_c();
        return b == Blocks.field_150365_q || b == Blocks.field_150366_p || b == Blocks.field_150482_ag || b == Blocks.field_150352_o || b == Blocks.field_150369_x || b == Blocks.field_150412_bA || b == Blocks.field_150450_ax;
    }

    public static boolean canDig(World world, BlockPos pos) {
        return !world.func_180495_p(pos.func_177984_a()).func_185904_a().func_76224_d() && !world.func_180495_p(pos.func_177976_e()).func_185904_a().func_76224_d() && !world.func_180495_p(pos.func_177974_f()).func_185904_a().func_76224_d() && !world.func_180495_p(pos.func_177978_c()).func_185904_a().func_76224_d() && !world.func_180495_p(pos.func_177968_d()).func_185904_a().func_76224_d();
    }

    public boolean canMine() {
        if (this.miningPos == null) {
            return false;
        }
        if (!this.village.isInVillage(this.miningPos.func_177967_a(this.signFacing, -1))) {
            return false;
        }
        if (this.world.func_180495_p(this.miningPos).func_185904_a().func_76224_d()) {
            return false;
        }
        if (this.world.func_180495_p(this.miningPos.func_177984_a()).func_185904_a().func_76224_d()) {
            return false;
        }
        if (VillageStructureMineshaft.isSolid(this, this.miningPos) && !VillageStructureMineshaft.canDig(this.world, this.miningPos)) {
            return false;
        }
        return !VillageStructureMineshaft.isSolid(this, this.miningPos.func_177984_a()) || VillageStructureMineshaft.canDig(this.world, this.miningPos.func_177984_a());
    }

    @Override
    protected BlockPos findDoor() {
        BlockPos dp = null;
        dp = this.framePos.func_177967_a(this.signFacing, -1).func_177967_a(this.signFacing.func_176746_e(), 1).func_177977_b();
        if (!VillageStructureMineshaft.isTunnel(this, dp)) {
            dp = this.framePos.func_177967_a(this.signFacing, -1).func_177967_a(this.signFacing.func_176746_e(), -1).func_177977_b();
            if (!VillageStructureMineshaft.isTunnel(this, dp)) {
                dp = this.framePos.func_177967_a(this.signFacing, -1).func_177979_c(2);
            }
            if (!VillageStructureMineshaft.isTunnel(this, dp)) {
                dp = null;
            }
        }
        return dp;
    }

    public static boolean isTunnel(VillageStructureMineshaft mineshaft, BlockPos floorPos) {
        BlockPos upPos = floorPos.func_177984_a();
        return VillageStructureMineshaft.isAir(mineshaft, upPos) && VillageStructureMineshaft.isAir(mineshaft, floorPos) && VillageStructureMineshaft.isSolid(mineshaft, upPos.func_177984_a()) && VillageStructureMineshaft.isSolid(mineshaft, floorPos.func_177977_b()) && VillageStructureMineshaft.isSolid(mineshaft, upPos.func_177967_a(mineshaft.signFacing.func_176746_e(), 1)) && VillageStructureMineshaft.isSolid(mineshaft, upPos.func_177967_a(mineshaft.signFacing.func_176746_e(), -1)) && VillageStructureMineshaft.isSolid(mineshaft, floorPos.func_177967_a(mineshaft.signFacing.func_176746_e(), 1)) && VillageStructureMineshaft.isSolid(mineshaft, floorPos.func_177967_a(mineshaft.signFacing.func_176746_e(), -1));
    }

    public static BlockPos getTunnelFlaw(VillageStructureMineshaft mineshaft, BlockPos pos) {
        BlockPos testPos = pos.func_177977_b();
        if (!VillageStructureMineshaft.isSolid(mineshaft, testPos)) {
            return testPos;
        }
        testPos = pos.func_177967_a(mineshaft.signFacing.func_176746_e(), 1);
        if (!VillageStructureMineshaft.isSolid(mineshaft, testPos)) {
            return testPos;
        }
        testPos = pos.func_177967_a(mineshaft.signFacing.func_176746_e(), -1);
        if (!VillageStructureMineshaft.isSolid(mineshaft, testPos)) {
            return testPos;
        }
        testPos = pos.func_177984_a().func_177967_a(mineshaft.signFacing.func_176746_e(), 1);
        if (!VillageStructureMineshaft.isSolid(mineshaft, testPos)) {
            return testPos;
        }
        testPos = pos.func_177984_a().func_177967_a(mineshaft.signFacing.func_176746_e(), -1);
        if (!VillageStructureMineshaft.isSolid(mineshaft, testPos)) {
            return testPos;
        }
        testPos = pos.func_177981_b(2);
        if (!VillageStructureMineshaft.isSolid(mineshaft, testPos)) {
            return testPos;
        }
        return null;
    }

    public static boolean isAir(VillageStructureMineshaft mineshaft, BlockPos pos) {
        return mineshaft.world.func_180495_p(pos).func_185904_a() == Material.field_151579_a || mineshaft.world.func_180495_p(pos).func_177230_c() == Blocks.field_150478_aa;
    }

    public static boolean isSolid(VillageStructureMineshaft mineshaft, BlockPos pos) {
        return mineshaft.world.func_180495_p(pos).func_185913_b();
    }

    @Override
    protected void scanFloor(BlockPos pos) {
        if (!this.floorTiles.contains(pos) && VillageStructureMineshaft.isTunnel(this, pos) && this.village.getPathingGraph().isInGraph(pos)) {
            this.miningPos = pos.func_177967_a(this.signFacing, -1);
            this.ceilingHeightSum += 2;
            this.addFloorTile(pos);
            this.scanFloor(pos.func_177967_a(this.signFacing, -1));
        }
    }

    private void addFloorTile(BlockPos pos) {
        this.floorTiles.add(pos);
        this.aabb = this.aabb.func_111270_a(new AxisAlignedBB(pos));
    }

    @Override
    public boolean validate() {
        this.isValid = true;
        if (this.door == null) {
            this.isValid = false;
        }
        Entity e = this.world.func_73045_a(this.signEntityId);
        if (this.isValid && (e == null || !(e instanceof EntityItemFrame))) {
            this.debugOut("Mineshaft struct frame is missing or wrong type");
            this.isValid = false;
        }
        EntityItemFrame itemFrame = (EntityItemFrame)e;
        if (this.isValid && itemFrame.func_174857_n() != this.framePos) {
            this.debugOut("Mineshaft struct center has moved");
            this.isValid = false;
        }
        if (this.isValid && !itemFrame.func_82335_i().func_77969_a(this.type.itemStack)) {
            this.debugOut("Mineshaft struct frame item has changed");
            this.isValid = false;
        }
        if (!this.canMine()) {
            this.isValid = false;
        }
        return this.isValid;
    }
}

