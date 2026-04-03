/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.item.ItemStack
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityAIThorSlam
extends EntityAIBase {
    private int attackTick = 0;
    private final int ATTACK_TICK_TIME = 80;
    private final int COOLDOWN = 200;
    private final double attackRange;
    private final Function<EntityVillagerTek, ItemStack> weaponFunc;
    private ItemStack weapon;
    protected final EntityGuard guard;
    private final Predicate<EntityVillagerTek> shouldPred;
    private long nextAttackTick = 0L;

    public EntityAIThorSlam(EntityGuard entityIn, Function<EntityVillagerTek, ItemStack> func, double range, Predicate<EntityVillagerTek> shouldPred) {
        this.func_75248_a(1);
        this.shouldPred = shouldPred;
        this.guard = entityIn;
        this.attackRange = range;
        this.weaponFunc = func;
    }

    public boolean func_75250_a() {
        if (this.guard.func_70638_az() != null && this.guard.isAIFilterEnabled("guard_super_attack")) {
            if (this.guard.isStarving()) {
                return false;
            }
            if (this.guard.field_70170_p.func_82737_E() < this.nextAttackTick) {
                return false;
            }
            if (!this.shouldPred.test(this.guard)) {
                return false;
            }
            List<EntityLivingBase> hostiles = this.getTargets();
            if (hostiles.size() >= 2 && this.guard.func_70681_au().nextInt(2) == 0) {
                this.weapon = this.weaponFunc.apply(this.guard);
                if (this.weapon.func_190926_b()) {
                    this.guard.setThought(EntityVillagerTek.VillagerThought.SWORD);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private List<EntityLivingBase> getTargets() {
        List hostiles = this.guard.field_70170_p.func_175647_a(EntityLivingBase.class, this.guard.func_174813_aQ().func_186662_g(this.attackRange), this.guard.isSuitableTarget());
        return hostiles;
    }

    public boolean func_75252_g() {
        return this.attackTick <= 0;
    }

    public void func_75249_e() {
        super.func_75249_e();
        this.tryAttack();
    }

    protected void tryAttack() {
        if (this.weapon != null && this.guard.func_70089_S()) {
            this.attackTick = 80;
            this.nextAttackTick = this.guard.field_70170_p.func_82737_E() + (long)this.guard.getSkillLerp(ProfessionType.GUARD, 200, 100);
            this.guard.func_70625_a((Entity)this.guard.func_70638_az(), 60.0f, 40.0f);
            this.guard.equipActionItem(this.weapon);
            this.guard.playServerAnimation("villager_thor_jump");
            if (this.guard.getHunger() > 2) {
                this.guard.modifyHunger(-1);
            }
            this.guard.playSound(ModSoundEvents.bigAttack);
            this.guard.func_70661_as().func_75499_g();
            this.guard.addJob(new TickJob(40, 0, false, () -> {
                if (this.weapon != null && this.guard.func_70089_S()) {
                    double knockbackMod = (double)this.guard.getSkillLerp(ProfessionType.GUARD, 200, 400) / 100.0;
                    List<EntityLivingBase> hostiles = this.getTargets();
                    hostiles.forEach(e -> this.guard.attackEntityAsMob((Entity)e, knockbackMod));
                    this.guard.damageItem(this.weapon, 1);
                    this.guard.tryAddSkill(ProfessionType.GUARD, 1);
                    this.guard.debugOut("Melee weapon: " + this.weapon.func_77952_i() + " / " + this.weapon.func_77958_k());
                    this.guard.playSound(ModSoundEvents.slamGround);
                }
            }));
        }
    }

    public void func_75246_d() {
        --this.attackTick;
        super.func_75246_d();
    }

    public void func_75251_c() {
        this.guard.unequipActionItem(this.weapon);
        this.guard.stopServerAnimation("villager_thor_jump");
        this.attackTick = 0;
        this.guard.pickupItems(5);
        this.weapon = null;
        super.func_75251_c();
    }

    public boolean func_75253_b() {
        if (this.attackTick > 0) {
            return true;
        }
        return super.func_75253_b();
    }
}

