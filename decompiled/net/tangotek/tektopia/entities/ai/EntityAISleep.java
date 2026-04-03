/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;

public class EntityAISleep
extends EntityAIMoveToBlock {
    private int bedAxis = -1;
    protected final EntityVillagerTek villager;
    private boolean hasSleptToday = false;

    public EntityAISleep(EntityVillagerTek v) {
        super(v);
        this.villager = v;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        if (this.villager.getBedPos() != null) {
            if (this.isWalkable(this.villager.getBedPos().func_177984_a(), this.navigator)) {
                return this.villager.getBedPos().func_177984_a();
            }
            return this.villager.getBedPos();
        }
        return null;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick()) {
            if (!this.villager.isSleepingTime()) {
                this.hasSleptToday = false;
            }
            if (this.villager.hasVillage() && this.villager.getVillage().enemySeenRecently()) {
                return false;
            }
            if (this.villager.shouldSleep()) {
                this.bedAxis = this.getBedAxis();
                if (this.bedAxis >= 0) {
                    if (!this.hasSleptToday) {
                        return super.func_75250_a();
                    }
                    this.villager.setThought(EntityVillagerTek.VillagerThought.INSOMNIA);
                } else {
                    this.villager.setThought(EntityVillagerTek.VillagerThought.BED);
                }
            }
        }
        return false;
    }

    @Override
    public void func_75249_e() {
        super.func_75249_e();
    }

    @Override
    public boolean func_75253_b() {
        if (!this.villager.shouldSleep()) {
            return false;
        }
        if (this.villager.isSleeping() && this.bedAxis < 0) {
            return false;
        }
        if (this.hasArrived()) {
            return this.villager.isSleeping();
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    public void func_75246_d() {
        if (this.villager.isSleeping()) {
            this.hasSleptToday = true;
            if (this.villager.func_70681_au().nextInt(1200) == 0) {
                this.villager.func_70691_i(1.0f);
            }
            if (this.villager.func_70681_au().nextInt(30) == 0) {
                this.startSleep();
            }
        }
        super.func_75246_d();
    }

    @Override
    protected BlockPos findWalkPos() {
        return this.destinationPos;
    }

    private int getBedAxis() {
        if (this.villager.getBedPos() != null) {
            EnumFacing enumfacing;
            IBlockState state = this.villager.field_70170_p.func_175667_e(this.villager.getBedPos()) ? this.villager.field_70170_p.func_180495_p(this.villager.getBedPos()) : null;
            boolean isBed = state != null && state.func_177230_c().isBed(state, (IBlockAccess)this.villager.field_70170_p, this.villager.getBedPos(), null);
            EnumFacing enumFacing = enumfacing = isBed && state.func_177230_c() instanceof BlockHorizontal ? (EnumFacing)state.func_177229_b((IProperty)BlockHorizontal.field_185512_D) : null;
            if (enumfacing != null) {
                return enumfacing.func_176736_b();
            }
        }
        return -1;
    }

    private Vec3d getSleepPos() {
        BlockPos bp = this.villager.getBedPos().func_177984_a();
        return new Vec3d((double)bp.func_177958_n() + 0.5, (double)bp.func_177956_o() - 0.3, (double)bp.func_177952_p() + 0.5);
    }

    private void moveToSleepPos() {
        Vec3d sleepPos = this.getSleepPos();
        if (this.villager.func_174791_d().func_186679_c(sleepPos.field_72450_a, sleepPos.field_72448_b, sleepPos.field_72449_c) > 0.05) {
            this.villager.func_70012_b(sleepPos.field_72450_a, sleepPos.field_72448_b, sleepPos.field_72449_c, this.villager.field_70177_z, this.villager.field_70125_A);
            this.villager.field_70159_w = 0.0;
            this.villager.field_70181_x = 0.0;
            this.villager.field_70179_y = 0.0;
        }
    }

    @Override
    protected void onArrival() {
        this.startSleep();
        super.onArrival();
    }

    private void startSleep() {
        this.bedAxis = this.getBedAxis();
        if (this.bedAxis >= 0) {
            this.moveToSleepPos();
            this.villager.onStartSleep(this.bedAxis);
        }
    }

    @Override
    public void func_75251_c() {
        this.villager.onStopSleep();
        this.bedAxis = -1;
        super.func_75251_c();
    }
}

