/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.IWorldEventListener
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.pathing;

import javax.annotation.Nullable;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.tangotek.tektopia.VillageManager;

public class PathingWorldListener
implements IWorldEventListener {
    protected final VillageManager villageManager;

    public PathingWorldListener(VillageManager vm) {
        this.villageManager = vm;
    }

    public void func_184376_a(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        if (this.didBlockChange(worldIn, pos, oldState, newState)) {
            this.villageManager.onBlockUpdate(worldIn, pos);
        }
    }

    public void func_174959_b(BlockPos pos) {
    }

    public void func_147585_a(int x1, int y1, int z1, int x2, int y2, int z2) {
    }

    public void func_184375_a(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
    }

    public void func_184377_a(SoundEvent soundIn, BlockPos pos) {
    }

    public void func_180442_a(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
    }

    public void func_190570_a(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
    }

    public void func_72703_a(Entity entityIn) {
    }

    public void func_72709_b(Entity entityIn) {
    }

    public void func_180440_a(int soundID, BlockPos pos, int data) {
    }

    public void func_180439_a(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
    }

    public void func_180441_b(int breakerId, BlockPos pos, int progress) {
    }

    protected boolean didBlockChange(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState) {
        AxisAlignedBB axisalignedbb1;
        if (newState.func_177230_c() instanceof BlockDoor) {
            return false;
        }
        if (oldState.func_185904_a().func_76224_d() != newState.func_185904_a().func_76224_d()) {
            return true;
        }
        AxisAlignedBB axisalignedbb = oldState.func_185890_d((IBlockAccess)worldIn, pos);
        return axisalignedbb != (axisalignedbb1 = newState.func_185890_d((IBlockAccess)worldIn, pos)) && (axisalignedbb == null || !axisalignedbb.equals((Object)axisalignedbb1));
    }
}

