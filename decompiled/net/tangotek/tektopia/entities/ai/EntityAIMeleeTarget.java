/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraft.item.ItemStack
 */
package net.tangotek.tektopia.entities.ai;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIFollow;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityAIMeleeTarget
extends EntityAIFollow {
    private int attackTick = 0;
    private final int ATTACK_TICK_TIME = 24;
    private int attackTime = 24;
    private boolean arrived = false;
    private final double attackRange;
    private final Function<EntityVillagerTek, ItemStack> weaponFunc;
    private ItemStack weapon;
    private Runnable onHit;
    private final ProfessionType professionType;
    protected final EntityVillagerTek villager;
    private final Predicate<EntityVillagerTek> shouldPred;
    private final EntityVillagerTek.VillagerThought missingThought;

    public EntityAIMeleeTarget(EntityVillagerTek entityIn, Function<EntityVillagerTek, ItemStack> func, EntityVillagerTek.VillagerThought missingThought, Predicate<EntityVillagerTek> shouldPred, Runnable onHit, ProfessionType pt) {
        super(entityIn);
        this.shouldPred = shouldPred;
        this.villager = entityIn;
        this.attackRange = 3.5;
        this.weaponFunc = func;
        this.onHit = onHit;
        this.professionType = pt;
        this.missingThought = missingThought;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.getHunger() < 10) {
            return false;
        }
        if (!this.shouldPred.test(this.villager)) {
            return false;
        }
        if (super.func_75250_a()) {
            this.weapon = this.weaponFunc.apply(this.villager);
            if (this.weapon.func_190926_b()) {
                this.villager.setThought(this.missingThought);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onArrival() {
        if (!this.arrived) {
            this.tryAttack();
        }
        this.arrived = true;
        super.onArrival();
    }

    @Override
    public void func_75249_e() {
        this.villager.debugOut("Melee Start @" + this.villager.func_180425_c());
        this.villager.equipActionItem(this.weapon);
        super.func_75249_e();
    }

    protected boolean inRange() {
        if (this.getFollowTarget() != null) {
            double distSq;
            double moddedAttackRange = this.attackRange * (double)this.villager.getSkillLerp(this.professionType, 100, 125) / 100.0;
            if (this.villager.func_70638_az() instanceof EntityArmorStand) {
                moddedAttackRange = 1.6;
            }
            return (distSq = this.getFollowTarget().func_70068_e((Entity)this.villager)) < moddedAttackRange * moddedAttackRange;
        }
        return false;
    }

    @Override
    protected boolean isNearWalkPos() {
        return this.inRange();
    }

    protected void tryAttack() {
        if (this.weapon != null && this.villager.func_70089_S() && this.followTarget.func_70089_S() && this.inRange()) {
            this.attackTime = this.attackTick = this.villager.getSkillLerp(this.professionType, 28, 24);
            this.navigator.func_70625_a((Entity)this.getFollowTarget(), 60.0f, 40.0f);
            this.villager.debugOut("Melee Attack Anim Start - " + this.getFollowTarget().func_145782_y());
            this.villager.playServerAnimation("villager_chop");
            if (this.villager.getHunger() > 2 && this.villager.func_70681_au().nextBoolean()) {
                this.villager.modifyHunger(-1);
            }
            this.villager.func_70661_as().func_75499_g();
            this.setArrived();
            this.villager.addJob(new TickJob(16, 0, false, () -> {
                if (this.weapon != null && this.getFollowTarget() != null && this.getFollowTarget().func_70089_S() && this.villager.func_70089_S()) {
                    this.villager.func_70652_k((Entity)this.getFollowTarget());
                    if (!(this.villager.func_70638_az() instanceof EntityArmorStand)) {
                        this.villager.damageItem(this.weapon, 3);
                    }
                    this.onHit.run();
                    this.villager.playSound(ModSoundEvents.villagerGrunt);
                    this.villager.throttledSadness(-1);
                }
            }));
        }
    }

    @Override
    public void func_75246_d() {
        if (this.attackTick > 0) {
            --this.attackTick;
            if (this.attackTime - this.attackTick == 20) {
                this.villager.stopServerAnimation("villager_chop");
            }
        } else if (this.inRange()) {
            this.tryAttack();
        }
        super.func_75246_d();
    }

    @Override
    protected EntityLivingBase getFollowTarget() {
        return this.villager.func_70638_az();
    }

    @Override
    protected boolean shouldFollow() {
        if (this.attackTick > 0) {
            return false;
        }
        return super.shouldFollow();
    }

    @Override
    public void func_75251_c() {
        this.villager.unequipActionItem(this.weapon);
        this.villager.stopServerAnimation("villager_chop");
        this.villager.debugOut("Melee End");
        this.attackTick = 0;
        this.villager.pickupItems(5);
        this.arrived = false;
        this.weapon = null;
        super.func_75251_c();
    }

    @Override
    public boolean func_75253_b() {
        if (this.attackTick > 0) {
            return true;
        }
        if (this.villager.func_70638_az() == null) {
            return false;
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        if (this.villager.isHostile().test((Object)this.followTarget)) {
            this.villager.setMovementMode(EntityVillagerTek.MovementMode.RUN);
        } else {
            this.villager.setMovementMode(EntityVillagerTek.MovementMode.WALK);
        }
    }
}

