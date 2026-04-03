/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.entities.EntityDeathCloud;
import net.tangotek.tektopia.entities.EntityNecromancer;

public class EntityAIDeathCloud
extends EntityAIBase {
    private EntityNecromancer necro;
    private EntityPlayer targetPlayer;
    private int cooldownTick = 0;
    private int castTime;
    private BlockPos targetPos;
    private final Predicate<Entity> entityPredicate;

    public EntityAIDeathCloud(EntityNecromancer n) {
        this.necro = n;
        this.func_75248_a(1);
        this.entityPredicate = Predicates.and((Predicate)EntitySelectors.field_188444_d, e -> e.func_70089_S() && this.necro.func_70635_at().func_75522_a(e));
    }

    public boolean func_75250_a() {
        if (this.necro.isAITick() && this.necro.field_70173_aa > this.cooldownTick && !this.necro.hasVillagerDied()) {
            this.targetPlayer = this.necro.field_70170_p.func_190525_a(this.necro.field_70165_t, this.necro.field_70163_u, this.necro.field_70161_v, 16.0, this.entityPredicate);
            if (this.targetPlayer != null) {
                this.targetPos = this.targetPlayer.func_180425_c();
                if (!this.targetPlayer.field_70122_E && this.necro.field_70170_p.func_180495_p(this.targetPos.func_177977_b()).func_177230_c().equals(Blocks.field_150350_a)) {
                    this.targetPos = this.targetPos.func_177977_b();
                }
                return true;
            }
        }
        return false;
    }

    public void func_75249_e() {
        this.startCast();
        super.func_75249_e();
    }

    public boolean func_75253_b() {
        return this.castTime > 0;
    }

    public boolean func_75252_g() {
        return false;
    }

    public void func_75246_d() {
        if (this.castTime > 0) {
            --this.castTime;
            if (this.castTime >= 37) {
                if (this.castTime == 37) {
                    this.createCloud();
                } else if (this.castTime == 33) {
                    this.necro.func_184185_a(ModSoundEvents.deathCircle, this.necro.field_70170_p.field_73012_v.nextFloat() * 0.4f + 1.2f, this.necro.field_70170_p.field_73012_v.nextFloat() * 0.4f + 0.8f);
                }
                if (this.castTime > 70) {
                    this.necro.func_70625_a((Entity)this.targetPlayer, 30.0f, 30.0f);
                }
            }
            if (this.castTime == 0) {
                this.necro.stopServerAnimation("necro_cast_forward");
            }
        }
        super.func_75246_d();
    }

    private void createCloud() {
        EntityDeathCloud cloud = new EntityDeathCloud(this.necro.field_70170_p, this.targetPos.func_177958_n(), this.targetPos.func_177956_o(), this.targetPos.func_177952_p());
        cloud.func_184483_a(3.0f);
        cloud.func_184485_d(10);
        cloud.func_184486_b(40 + this.necro.getLevel() * 30);
        cloud.func_184487_c(0.03f);
        this.necro.field_70170_p.func_72838_d((Entity)cloud);
        this.necro.playSound(ModSoundEvents.deathSummonEnd);
    }

    private void startCast() {
        this.cooldownTick = this.necro.field_70173_aa + this.necro.getLevelCooldown(this.necro.func_70681_au().nextInt(160) + 160);
        this.castTime = 80;
        this.necro.func_70661_as().func_75499_g();
        this.necro.playServerAnimation("necro_cast_forward");
    }

    private void stopCast() {
        this.necro.stopServerAnimation("necro_cast_forward");
    }

    public void func_75251_c() {
        this.stopCast();
        this.castTime = 0;
        super.func_75251_c();
    }
}

