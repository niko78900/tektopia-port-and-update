/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityChild;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIFollow;

public class EntityAIPlayTag
extends EntityAIFollow {
    protected EntityChild targetChild = null;
    protected EntityChild thisChild = null;
    protected EntityChild preferredTarget = null;
    private boolean active = false;
    protected final Predicate<Entity> canChase;
    protected final EntityVillagerTek villager;
    private int activeTick = 0;

    public EntityAIPlayTag(EntityVillagerTek v) {
        super(v);
        this.villager = v;
        Predicate isChasable = e -> e.func_70089_S() && e instanceof EntityChild;
        this.canChase = Predicates.and((Predicate)EntitySelectors.field_188444_d, (Predicate)isChasable);
        if (this.villager instanceof EntityChild) {
            this.thisChild = (EntityChild)this.villager;
        }
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick("play_tag") && this.villager.hasVillage() && this.villager.isWorkTime() && this.thisChild != null) {
            if (this.thisChild.getChasedBy() != null) {
                this.preferredTarget = this.thisChild.getChasedBy();
            } else {
                this.targetChild = this.seekChaseTarget();
                if (this.targetChild != null) {
                    return super.func_75250_a();
                }
            }
        }
        return false;
    }

    @Override
    protected EntityLivingBase getFollowTarget() {
        return this.targetChild;
    }

    private EntityChild seekChaseTarget() {
        List children;
        EntityChild result = null;
        if (this.preferredTarget != null) {
            if (this.canChase.test((Object)this.preferredTarget) && this.thisChild.func_70068_e((Entity)this.preferredTarget) < 256.0) {
                result = this.preferredTarget;
            }
            this.preferredTarget = null;
        }
        if (result == null && !(children = this.villager.field_70170_p.func_175674_a((Entity)this.villager, this.villager.func_174813_aQ().func_186662_g(16.0), this.canChase)).isEmpty()) {
            return (EntityChild)((Object)children.get(this.villager.field_70170_p.field_73012_v.nextInt(children.size())));
        }
        return result;
    }

    @Override
    public void func_75249_e() {
        this.active = true;
        this.activeTick = this.villager.field_70170_p.field_73012_v.nextInt(100) + 80;
        this.villager.setMovementMode(EntityVillagerTek.MovementMode.RUN);
        this.targetChild.setChasedBy((EntityChild)this.villager);
        if (this.villager.func_70681_au().nextInt(4) == 0) {
            this.villager.modifyHappy(4);
        }
        super.func_75249_e();
    }

    @Override
    public boolean func_75253_b() {
        return this.targetChild != null && this.active;
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(EntityVillagerTek.MovementMode.RUN);
    }

    @Override
    public void func_75246_d() {
        --this.activeTick;
        if (this.activeTick <= 0) {
            this.active = false;
        }
        super.func_75246_d();
    }

    @Override
    protected void onArrival() {
        super.onArrival();
    }

    @Override
    protected void onStuck() {
        this.active = false;
        super.onStuck();
    }

    @Override
    protected void onPathFailed(BlockPos pos) {
        this.active = false;
        super.onPathFailed(pos);
    }

    @Override
    public void func_75251_c() {
        this.active = false;
        this.targetChild.setChasedBy(null);
        this.preferredTarget = null;
        this.villager.setMovementMode(this.villager.getDefaultMovement());
        this.targetChild = null;
        super.func_75251_c();
    }
}

