/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 */
package net.tangotek.tektopia.entities.ai;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.tangotek.tektopia.ModPotions;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityCleric;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIFollow;

public class EntityAIBless
extends EntityAIFollow {
    private boolean arrived = false;
    private int castTime = 0;
    private EntityCleric cleric;
    private final int CAST_TIME = 42;
    private final double range;
    private EntityVillagerTek target = null;
    private int cooldownTick = 0;
    private final int COOLDOWN = 1200;

    public EntityAIBless(EntityVillagerTek entityIn, double range) {
        super(entityIn);
        this.cleric = (EntityCleric)entityIn;
        this.range = range;
    }

    @Override
    public boolean func_75250_a() {
        List candidates;
        if (this.isCooldownReady() && this.cleric.isAITick("cast_bless") && this.cleric.hasVillage() && !this.cleric.shouldSleep() && !(candidates = this.cleric.field_70170_p.func_175647_a(EntityVillagerTek.class, this.cleric.func_174813_aQ().func_72314_b(25.0, 8.0, 25.0), this.isBlessable())).isEmpty()) {
            this.target = (EntityVillagerTek)((Object)candidates.get(0));
            if (this.target == this.cleric && candidates.size() > 1) {
                this.target = (EntityVillagerTek)((Object)candidates.get(1));
            }
            return super.func_75250_a();
        }
        return false;
    }

    private Predicate<EntityVillagerTek> isBlessable() {
        return v -> v.getHappy() < v.getMaxHappy() && v != this.cleric && v.getBlessed() <= 0 && v.isRole(VillagerRole.VILLAGER) && !v.isSleeping();
    }

    private boolean isCooldownReady() {
        return this.cleric.field_70173_aa - this.cooldownTick > 1200;
    }

    @Override
    protected void onArrival() {
        if (!this.arrived) {
            this.tryCast();
        }
        this.arrived = true;
        super.onArrival();
    }

    protected boolean inRange() {
        if (this.getFollowTarget() != null) {
            double distSq = this.getFollowTarget().func_70068_e((Entity)this.cleric);
            return distSq < this.range * this.range;
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

    protected void tryCast() {
        if (this.cleric.func_70089_S() && this.followTarget.func_70089_S() && this.inRange()) {
            this.castTime = 42;
            this.cleric.playServerAnimation("villager_cast_bless");
            this.cleric.modifyHunger(-3);
            this.cleric.func_70661_as().func_75499_g();
            this.setArrived();
            this.cleric.setSpellTarget((Entity)this.target);
            this.cleric.playSound(ModSoundEvents.healingSource);
        }
    }

    @Override
    public void func_75246_d() {
        --this.castTime;
        if (this.castTime == 0) {
            this.cleric.playSound(ModSoundEvents.villagerEnchant);
            if (this.target.func_70089_S()) {
                PotionEffect blessEffect = new PotionEffect((Potion)ModPotions.potionBless, 6000, this.cleric.getBaseSkill(ProfessionType.CLERIC));
                this.target.func_70690_d(blessEffect);
                this.target.playSound(ModSoundEvents.healingTarget);
                this.cleric.tryAddSkill(ProfessionType.CLERIC, 4);
                this.cooldownTick = this.cleric.field_70173_aa;
            }
        }
        super.func_75246_d();
    }

    @Override
    protected EntityLivingBase getFollowTarget() {
        return this.target;
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
        this.cleric.stopServerAnimation("villager_cast_bless");
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
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        this.cleric.setMovementMode(this.cleric.getDefaultMovement());
    }
}

