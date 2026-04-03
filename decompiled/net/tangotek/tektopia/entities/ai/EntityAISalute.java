/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.entities.EntityGuard;

public class EntityAISalute
extends EntityAIBase {
    private EntityGuard guard;
    private EntityLivingBase saluteTarget;
    private long lastSalute = 0L;
    private int saluteTime = 0;

    public EntityAISalute(EntityGuard v) {
        this.guard = v;
        this.func_75248_a(1);
    }

    public boolean func_75250_a() {
        if (this.guard.isAITick("salute") && this.guard.field_70170_p.func_82737_E() - this.lastSalute > 2400L && !this.guard.isSleeping() && this.guard.func_70638_az() == null && this.guard.hasVillage() && !this.guard.getVillage().enemySeenRecently()) {
            List captains;
            this.saluteTarget = this.guard.field_70170_p.func_72890_a((Entity)this.guard, 12.0);
            if (this.saluteTarget != null) {
                return true;
            }
            if (!this.guard.isCaptain() && !(captains = this.guard.field_70170_p.func_175647_a(EntityGuard.class, this.guard.func_174813_aQ().func_186662_g(12.0), g -> g.isCaptain())).isEmpty()) {
                this.saluteTarget = (EntityLivingBase)captains.get(0);
                return true;
            }
        }
        return false;
    }

    public void func_75249_e() {
        this.startSalute();
        super.func_75249_e();
    }

    public boolean func_75253_b() {
        return this.saluteTime > 0;
    }

    public void func_75246_d() {
        if (this.saluteTime > 0) {
            if (this.saluteTime == 10) {
                this.guard.stopServerAnimation("villager_salute");
                if (this.guard.func_70681_au().nextInt(3) == 0) {
                    this.guard.modifyHappy(2);
                }
            }
            this.guard.func_70625_a((Entity)this.saluteTarget, 30.0f, 30.0f);
            --this.saluteTime;
        }
        super.func_75246_d();
    }

    private void startSalute() {
        this.lastSalute = this.guard.field_70170_p.func_82737_E();
        this.saluteTime = 50;
        this.guard.func_70661_as().func_75499_g();
        this.guard.equipActionItem(ModItems.EMPTY_HAND_ITEM);
        this.guard.playServerAnimation("villager_salute");
    }

    private void stopSalute() {
        this.guard.unequipActionItem(ModItems.EMPTY_HAND_ITEM);
        this.guard.stopServerAnimation("villager_salute");
    }

    public void func_75251_c() {
        this.stopSalute();
        this.saluteTime = 0;
        super.func_75251_c();
    }
}

