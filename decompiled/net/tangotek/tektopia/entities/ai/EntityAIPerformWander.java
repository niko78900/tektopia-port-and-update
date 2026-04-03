/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 */
package net.tangotek.tektopia.entities.ai;

import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityBard;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIFollow;

public class EntityAIPerformWander
extends EntityAIFollow {
    private boolean arrived = false;
    private int castTime = 0;
    private EntityVillagerTek target;
    private EntityBard bard;
    private int happyGiven = 0;

    public EntityAIPerformWander(EntityVillagerTek entityIn) {
        super(entityIn);
        this.bard = (EntityBard)entityIn;
    }

    @Override
    public boolean func_75250_a() {
        if (this.bard.isAITick("perform_wander") && this.bard.hasVillage() && this.bard.isWorkTime() && this.bard.func_70681_au().nextInt(8) == 0) {
            List villagers = this.bard.field_70170_p.func_175647_a(EntityVillagerTek.class, this.bard.func_174813_aQ().func_72314_b(30.0, 8.0, 30.0), v -> v.getHappy() < v.getMaxHappy() && !v.func_70028_i((Entity)this.bard) && v.isVillageMember(this.bard.getVillage()));
            this.target = villagers.stream().min(Comparator.comparing(v -> v.getHappy())).orElse(null);
            if (this.target != null) {
                return super.func_75250_a();
            }
        }
        return false;
    }

    @Override
    protected void onArrival() {
        if (!this.arrived) {
            this.tryPerform();
        }
        this.arrived = true;
        super.onArrival();
    }

    @Override
    public void func_75249_e() {
        this.bard.setPerformance(ModSoundEvents.Performance.getRandom(false, this.bard.func_70681_au()));
        super.func_75249_e();
    }

    protected boolean inRange() {
        if (this.getFollowTarget() != null) {
            double distSq = this.getFollowTarget().func_70068_e((Entity)this.bard);
            return distSq < 36.0;
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

    protected void tryPerform() {
        if (this.bard.func_70089_S() && this.followTarget.func_70089_S() && this.inRange()) {
            ModSoundEvents.Performance performance = this.bard.getPerformance();
            this.castTime = performance.duration;
            this.bard.func_70661_as().func_75499_g();
            this.bard.playServerAnimation(performance.anim);
            this.bard.func_184185_a(performance.sound, 2.0f, 1.0f);
            this.bard.modifyHunger(-6);
        }
    }

    @Override
    public void func_75246_d() {
        --this.castTime;
        if (this.castTime > 0 && this.bard.func_70681_au().nextInt(40) == 0) {
            this.distributeHappyNearby(8, 1, 1);
            this.bard.tryAddSkill(ProfessionType.BARD, 30);
        }
        if (this.castTime == 0) {
            this.distributeHappyNearby(2, 2, 3);
            if (this.happyGiven > 3) {
                this.bard.modifyHappy(this.happyGiven);
            } else {
                this.bard.modifyHappy(-2);
            }
        }
        super.func_75246_d();
    }

    private void distributeHappyNearby(int chance, int min, int max) {
        List nearby = this.bard.field_70170_p.func_175647_a(EntityVillagerTek.class, this.bard.func_174813_aQ().func_72314_b(16.0, 8.0, 16.0), v -> v.getHappy() < v.getMaxHappy() && v.isVillageMember(this.bard.getVillage()));
        for (EntityVillagerTek v2 : nearby) {
            if (v2 == this.bard || this.bard.func_70681_au().nextInt(chance) != 0) continue;
            v2.modifyHappy(this.bard.getSkillLerp(ProfessionType.BARD, min, max));
            ++this.happyGiven;
        }
    }

    @Override
    protected EntityLivingBase getFollowTarget() {
        return this.target;
    }

    @Override
    protected boolean shouldFollow() {
        if (this.arrived) {
            return false;
        }
        return super.shouldFollow();
    }

    @Override
    public void func_75251_c() {
        this.bard.stopServerAnimation(this.bard.getPerformance().anim);
        this.bard.setPerformance(null);
        this.arrived = false;
        this.castTime = 0;
        this.happyGiven = 0;
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
        this.bard.setMovementMode(this.bard.getDefaultMovement());
    }
}

