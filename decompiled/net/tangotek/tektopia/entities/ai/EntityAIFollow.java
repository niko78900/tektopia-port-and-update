/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;

public abstract class EntityAIFollow
extends EntityAIMoveToBlock {
    protected EntityLivingBase followTarget;
    protected BlockPos lastTargetPos;
    protected int followUpdateTick = 40;
    protected final EntityVillageNavigator navigator;

    public EntityAIFollow(EntityVillageNavigator entityIn) {
        super(entityIn);
        this.navigator = entityIn;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        BlockPos pos = this.followTarget.func_180425_c();
        if (this.isWalkable(pos, this.navigator)) {
            return pos;
        }
        Vec3d diff = this.followTarget.func_174791_d().func_178788_d(new Vec3d((double)pos.func_177958_n() + 0.5, (double)pos.func_177956_o(), (double)pos.func_177952_p() + 0.5));
        BlockPos xOffset = pos.func_177963_a(Math.signum(diff.field_72450_a), 0.0, 0.0);
        if (this.isWalkable(xOffset, this.navigator)) {
            return xOffset;
        }
        BlockPos zOffset = pos.func_177963_a(0.0, 0.0, Math.signum(diff.field_72449_c));
        if (this.isWalkable(zOffset, this.navigator)) {
            return zOffset;
        }
        BlockPos bothOffset = pos.func_177963_a(Math.signum(diff.field_72450_a), 0.0, Math.signum(diff.field_72449_c));
        if (this.isWalkable(bothOffset, this.navigator)) {
            return bothOffset;
        }
        return pos;
    }

    @Override
    public boolean func_75250_a() {
        if (this.navigator.hasVillage()) {
            this.followTarget = this.getFollowTarget();
            if (this.followTarget == null) {
                return false;
            }
            if (!this.shouldFollow()) {
                return false;
            }
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    public void func_75249_e() {
        this.updateFollowTick();
        super.func_75249_e();
    }

    @Override
    protected boolean isNearWalkPos() {
        return this.getWalkPos() != null && this.getWalkPos().func_177951_i((Vec3i)this.navigator.func_180425_c()) < 2.5;
    }

    @Override
    public boolean func_75253_b() {
        if (!this.shouldFollow()) {
            return false;
        }
        return super.func_75253_b();
    }

    @Override
    public void func_75246_d() {
        --this.followUpdateTick;
        if (this.shouldFollow()) {
            if (this.followUpdateTick <= 0) {
                this.updateFollowTick();
                this.doMove();
            } else if (this.followTarget.func_174818_b(this.lastTargetPos) > 25.0) {
                this.doMove();
            }
        }
        this.navigator.func_70625_a((Entity)this.followTarget, 60.0f, 40.0f);
        super.func_75246_d();
    }

    protected abstract EntityLivingBase getFollowTarget();

    @Override
    protected void doMove() {
        this.destinationPos = this.getDestinationBlock();
        super.doMove();
        this.lastTargetPos = this.followTarget.func_180425_c();
    }

    @Override
    public void func_75251_c() {
        this.followTarget = null;
        super.func_75251_c();
    }

    protected boolean shouldFollow() {
        return this.followTarget.func_70089_S();
    }

    private void updateFollowTick() {
        double distSq = this.followTarget.func_70068_e((Entity)this.navigator);
        this.followUpdateTick = distSq < 100.0 ? 10 : (distSq < 400.0 ? 40 : 60);
    }
}

