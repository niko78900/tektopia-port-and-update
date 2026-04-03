/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBed
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityBed
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;

public class VillageStructureHome
extends VillageStructure {
    private List<EntityVillagerTek> residents = new ArrayList<EntityVillagerTek>();
    private final int maxBeds;

    protected VillageStructureHome(World world, Village v, EntityItemFrame itemFrame, VillageStructureType structureType, String name, int maxBeds) {
        super(world, v, itemFrame, structureType, name);
        this.maxBeds = maxBeds;
    }

    @Override
    protected void setupServerJobs() {
        this.addJob(new TickJob(40, 40, true, () -> this.evictResidents()));
        super.setupServerJobs();
    }

    public List<EntityVillagerTek> getResidents() {
        return Collections.unmodifiableList(this.residents);
    }

    @Override
    public void onDestroy() {
        for (EntityVillagerTek resident : this.residents) {
            resident.clearHome();
        }
        ArrayList<BlockPos> beds = new ArrayList<BlockPos>(this.getSpecialBlocks(Blocks.field_150324_C));
        for (BlockPos bed : beds) {
            this.setBedColor(bed, EnumDyeColor.RED);
        }
        super.onDestroy();
    }

    public static boolean isBed(World w, BlockPos bp) {
        IBlockState state = w.func_175667_e(bp) ? w.func_180495_p(bp) : null;
        return state != null && state.func_177230_c().isBed(state, (IBlockAccess)w, bp, null);
    }

    public boolean isBedFoot(BlockPos bp) {
        if (VillageStructureHome.isBed(this.world, bp)) {
            return Blocks.field_150324_C.isBedFoot((IBlockAccess)this.world, bp) && this.world.func_175623_d(bp.func_177984_a());
        }
        return false;
    }

    @Override
    protected void scanSpecialBlock(BlockPos pos, Block block) {
        if (this.isBedFoot(pos)) {
            ArrayList<BlockPos> existingBeds = new ArrayList<BlockPos>(this.getSpecialBlocks(Blocks.field_150324_C));
            if (existingBeds.size() < this.maxBeds) {
                this.addSpecialBlock(Blocks.field_150324_C, pos);
                boolean claimed = false;
                for (EntityVillagerTek resident : this.residents) {
                    if (resident.getBedPos() == null || !resident.getBedPos().equals((Object)pos)) continue;
                    this.setBedColor(pos, EnumDyeColor.GREEN);
                    claimed = true;
                }
                if (!claimed) {
                    this.setBedColor(pos, EnumDyeColor.YELLOW);
                }
            } else {
                this.setBedColor(pos, EnumDyeColor.RED);
            }
        }
        super.scanSpecialBlock(pos, block);
    }

    public boolean canSleepAt(BlockPos pos) {
        for (EntityVillagerTek resident : this.residents) {
            BlockPos headPos;
            if (!resident.isSleeping()) continue;
            if (resident.getBedPos().equals((Object)pos)) {
                return false;
            }
            IBlockState blockState = this.village.getWorld().func_180495_p(resident.getBedPos());
            EnumFacing facing = (EnumFacing)blockState.func_177229_b((IProperty)BlockBed.field_185512_D);
            if (facing == null || !(headPos = resident.getBedPos().func_177972_a(facing)).equals((Object)pos)) continue;
            return false;
        }
        return true;
    }

    public boolean canVillagerSleep(EntityVillagerTek villager) {
        return true;
    }

    public void addResident(EntityVillagerTek villager) {
        if (!this.isFull() && !this.residents.contains((Object)villager)) {
            ArrayList<BlockPos> beds = new ArrayList<BlockPos>(this.getSpecialBlocks(Blocks.field_150324_C));
            for (EntityVillagerTek resident : this.residents) {
                ListIterator bedItr = beds.listIterator();
                while (bedItr.hasNext()) {
                    if (!((BlockPos)bedItr.next()).equals((Object)resident.getBedPos())) continue;
                    bedItr.remove();
                }
            }
            if (!beds.isEmpty()) {
                villager.setHome((BlockPos)beds.get(0), this.framePos);
                this.residents.add(villager);
                if (this.village != null && this.village.isValid()) {
                    this.setBedColor((BlockPos)beds.get(0), EnumDyeColor.GREEN);
                }
            }
        }
    }

    private void setBedColor(BlockPos footPos, EnumDyeColor color) {
        TileEntityBed bed;
        TileEntity tileEntity = this.village.getWorld().func_175625_s(footPos);
        if (tileEntity instanceof TileEntityBed && (bed = (TileEntityBed)tileEntity).func_193048_a() != color) {
            bed.func_193052_a(color);
            IBlockState blockState = this.village.getWorld().func_180495_p(footPos);
            EnumFacing facing = (EnumFacing)blockState.func_177229_b((IProperty)BlockBed.field_185512_D);
            BlockPos headPos = footPos.func_177972_a(facing);
            IBlockState headState = this.village.getWorld().func_180495_p(headPos);
            tileEntity = this.village.getWorld().func_175625_s(headPos);
            if (tileEntity instanceof TileEntityBed) {
                bed = (TileEntityBed)tileEntity;
                bed.func_193052_a(color);
            }
            this.village.getWorld().markAndNotifyBlock(footPos, null, blockState, blockState, 3);
            this.village.getWorld().markAndNotifyBlock(headPos, null, headState, headState, 3);
        }
    }

    public void evictResidents() {
        ListIterator<EntityVillagerTek> itr = this.residents.listIterator();
        while (itr.hasNext()) {
            EntityVillagerTek resident = itr.next();
            if (!resident.func_70089_S()) {
                itr.remove();
                continue;
            }
            if (resident.getBedPos() != null && this.isBedFoot(resident.getBedPos())) continue;
            resident.clearHome();
            itr.remove();
        }
    }

    public int getMaxResidents() {
        return this.getSpecialBlocks(Blocks.field_150324_C).size();
    }

    public int getCurResidents() {
        return this.residents.size();
    }

    public boolean isFull() {
        return this.getCurResidents() >= this.getMaxResidents();
    }

    @Override
    protected boolean shouldVillagerSit(EntityVillagerTek villager) {
        return this.world.field_73012_v.nextInt(2) == 0;
    }

    @Override
    public int getSitTime(EntityVillagerTek villager) {
        return 100 + villager.func_70681_au().nextInt(300);
    }

    public void villageReport(StringBuilder builder) {
        builder.append("        Home: " + this.getDoor() + "     " + this.getCurResidents() + " residents\n");
        for (EntityVillagerTek resident : this.residents) {
            builder.append("            " + ((Object)((Object)resident)).getClass().getSimpleName() + "|" + resident.func_145748_c_().func_150254_d() + "|" + resident.func_145782_y() + "        Happy: " + resident.getHappy() + "   Hunger: " + resident.getHunger() + "\n");
        }
    }
}

