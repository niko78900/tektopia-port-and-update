/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityCleric;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIFollow;

public class EntityAIHeal
extends EntityAIFollow {
    private boolean arrived = false;
    private int castTime = 0;
    private EntityCleric cleric;
    private final int CAST_TIME = 80;
    private final int HUNGER_COST = 8;
    private final double healRange;
    private EntityVillagerTek healVillager = null;

    public EntityAIHeal(EntityVillagerTek entityIn, double range) {
        super(entityIn);
        this.cleric = (EntityCleric)entityIn;
        this.healRange = range;
    }

    @Override
    public boolean func_75250_a() {
        this.healVillager = null;
        if (this.cleric.isAIFilterEnabled("cast_heal") && this.cleric.hasVillage() && !this.cleric.isSleeping() && this.cleric.getHunger() > 16) {
            List healCandidates;
            if (this.cleric.func_110143_aJ() < this.cleric.func_110138_aP()) {
                this.healVillager = this.cleric;
            }
            if (this.healVillager == null) {
                this.healVillager = this.cleric.getVillage().getActiveDefender(this.cleric.func_180425_c());
                if (this.healVillager != null && this.inRange() && this.healVillager.func_110143_aJ() >= this.healVillager.func_110138_aP()) {
                    this.healVillager = null;
                }
            }
            if (this.healVillager == null && this.cleric.isAITick() && !(healCandidates = this.cleric.field_70170_p.func_175647_a(EntityVillagerTek.class, this.cleric.func_174813_aQ().func_72314_b(30.0, 8.0, 30.0), v -> v.func_110143_aJ() < v.func_110138_aP())).isEmpty()) {
                this.healVillager = (EntityVillagerTek)((Object)healCandidates.get(0));
            }
            if (this.healVillager != null) {
                return super.func_75250_a();
            }
        }
        return false;
    }

    @Override
    protected void onArrival() {
        if (!this.arrived) {
            this.tryHeal();
        }
        this.arrived = true;
        super.onArrival();
    }

    @Override
    public void func_75249_e() {
        super.func_75249_e();
    }

    protected boolean inRange() {
        if (this.getFollowTarget() != null) {
            double distSq = this.getFollowTarget().func_70068_e((Entity)this.cleric);
            return distSq < this.healRange * this.healRange;
        }
        return false;
    }

    @Override
    protected boolean isNearWalkPos() {
        return this.inRange();
    }

    public boolean func_75252_g() {
        return this.castTime <= 0;
    }

    protected void tryHeal() {
        if (this.cleric.func_70089_S() && this.followTarget.func_70089_S() && this.inRange() && this.followTarget.func_110143_aJ() < this.followTarget.func_110138_aP()) {
            this.castTime = 80;
            this.cleric.playServerAnimation("villager_summon");
            this.cleric.modifyHunger(-8);
            this.cleric.func_70661_as().func_75499_g();
            this.setArrived();
            this.cleric.setSpellTarget((Entity)this.healVillager);
            this.cleric.playSound(ModSoundEvents.healingSource);
        }
    }

    @Override
    public void func_75246_d() {
        --this.castTime;
        if (this.castTime == 36) {
            this.cleric.playSound(ModSoundEvents.villagerEnchant);
            if (this.healVillager.func_70089_S() && this.healVillager.func_110143_aJ() < this.healVillager.func_110138_aP()) {
                this.healVillager.func_70691_i(this.cleric.getSkillLerp(ProfessionType.CLERIC, 3, 6));
                this.healVillager.playSound(ModSoundEvents.healingTarget);
                this.healVillager.modifyHappy(10);
                this.cleric.modifyHappy(2);
                this.cleric.tryAddSkill(ProfessionType.CLERIC, 3);
            }
        }
        super.func_75246_d();
    }

    @Override
    protected EntityLivingBase getFollowTarget() {
        return this.healVillager;
    }

    @Override
    protected boolean shouldFollow() {
        if (this.castTime > 0) {
            return false;
        }
        return super.shouldFollow();
    }

    @Override
    public void func_75251_c() {
        this.cleric.setMovementMode(this.cleric.getDefaultMovement());
        this.cleric.stopServerAnimation("villager_summon");
        this.cleric.setSpellTarget(null);
        this.arrived = false;
        this.castTime = 0;
        super.func_75251_c();
    }

    @Override
    public boolean func_75253_b() {
        if (this.castTime > 0) {
            return true;
        }
        if (!this.healVillager.func_70089_S()) {
            return false;
        }
        if (this.inRange() && this.healVillager.func_110143_aJ() >= this.healVillager.func_110138_aP()) {
            return false;
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        this.cleric.setMovementMode(EntityVillagerTek.MovementMode.RUN);
    }
}

