/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIBless;
import net.tangotek.tektopia.entities.ai.EntityAIFleeEntity;
import net.tangotek.tektopia.entities.ai.EntityAIHeal;
import net.tangotek.tektopia.entities.ai.EntityAIPatrolVillage;

public class EntityCleric
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityCleric.class);
    private static final DataParameter<Integer> SPELL_TARGET = EntityDataManager.func_187226_a(EntityCleric.class, (DataSerializer)DataSerializers.field_187192_b);
    private static final DataParameter<Boolean> CAST_BLESS = EntityDataManager.func_187226_a(EntityCleric.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> CAST_HEAL = EntityDataManager.func_187226_a(EntityCleric.class, (DataSerializer)DataSerializers.field_187198_h);
    private int handParticleTick = 0;

    public EntityCleric(World worldIn) {
        super(worldIn, ProfessionType.CLERIC, VillagerRole.VILLAGER.value | VillagerRole.DEFENDER.value);
        Runnable enchantRunner = new Runnable(){

            @Override
            public void run() {
                EntityCleric.this.field_70170_p.func_184134_a(EntityCleric.this.field_70165_t, EntityCleric.this.field_70163_u, EntityCleric.this.field_70161_v, ModSoundEvents.villagerEnchant, SoundCategory.NEUTRAL, 1.0f, EntityCleric.this.field_70146_Z.nextFloat() * 0.2f + 0.9f, false);
            }
        };
        if (this.field_70170_p.field_72995_K) {
            this.addAnimationTrigger("tektopia:villager_summon", 12, enchantRunner);
            this.addAnimationTrigger("tektopia:villager_summon", 20, new Runnable(){

                @Override
                public void run() {
                    EntityCleric.this.handParticleTick = 25;
                }
            });
            this.addAnimationTrigger("tektopia:villager_summon", 32, enchantRunner);
        }
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.addTask(49, new EntityAIHeal(this, 16.0));
        this.addTask(50, new EntityAIBless(this, 12.0));
        this.addTask(50, new EntityAIPatrolVillage(this, p -> !p.isSleepingTime()));
    }

    @Override
    public void attachToVillage(Village v) {
        super.attachToVillage(v);
        this.sleepOffset = v.getNextClericSleepOffset();
    }

    @Override
    protected void addTask(int priority, EntityAIBase task) {
        if (task instanceof EntityAIFleeEntity) {
            return;
        }
        super.addTask(priority, task);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_187214_a(SPELL_TARGET, 0);
        this.registerAIFilter("cast_bless", CAST_BLESS);
        this.registerAIFilter("cast_heal", CAST_HEAL);
    }

    @Override
    public EntityVillagerTek.MovementMode getDefaultMovement() {
        if (this.village.enemySeenRecently()) {
            return EntityVillagerTek.MovementMode.RUN;
        }
        return super.getDefaultMovement();
    }

    public boolean isSpellcasting() {
        int entityId = (Integer)this.field_70180_af.func_187225_a(SPELL_TARGET);
        return entityId > 0;
    }

    public Entity getSpellTargetEntity() {
        int entityId = (Integer)this.field_70180_af.func_187225_a(SPELL_TARGET);
        if (entityId > 0) {
            return this.field_70170_p.func_73045_a(entityId);
        }
        return null;
    }

    public void setSpellTarget(Entity entity) {
        if (entity == null) {
            this.field_70180_af.func_187227_b(SPELL_TARGET, 0);
        } else {
            this.field_70180_af.func_187227_b(SPELL_TARGET, entity.func_145782_y());
        }
    }

    public void func_70071_h_() {
        super.func_70071_h_();
        if (this.field_70170_p.field_72995_K && this.isSpellcasting() && this.handParticleTick > 0) {
            Entity spellTarget;
            double d0 = 0.7;
            double d1 = 0.7;
            double d2 = 0.7;
            float f = this.field_70761_aq * ((float)Math.PI / 180) + MathHelper.func_76134_b((float)((float)this.field_70173_aa * 0.6662f)) * 0.25f;
            float f1 = MathHelper.func_76134_b((float)f);
            float f2 = MathHelper.func_76126_a((float)f);
            this.field_70170_p.func_175688_a(EnumParticleTypes.SPELL_MOB, this.field_70165_t + (double)f1 * 0.6, this.field_70163_u + 2.0, this.field_70161_v + (double)f2 * 0.6, d0, d1, d2, new int[]{0, 1, 1});
            this.field_70170_p.func_175688_a(EnumParticleTypes.SPELL_MOB, this.field_70165_t - (double)f1 * 0.6, this.field_70163_u + 2.0, this.field_70161_v - (double)f2 * 0.6, d0, d1, d2, new int[]{0, 1, 1});
            --this.handParticleTick;
            if (this.handParticleTick <= 4 && (spellTarget = this.getSpellTargetEntity()) != null) {
                for (int i = 0; i < 20; ++i) {
                    double dx = this.func_70681_au().nextGaussian() * 0.5;
                    double dz = this.func_70681_au().nextGaussian() * 0.5;
                    this.field_70170_p.func_175688_a(EnumParticleTypes.VILLAGER_HAPPY, spellTarget.field_70165_t + dx, spellTarget.field_70163_u + this.func_70681_au().nextDouble() * 2.0, spellTarget.field_70161_v + dz, 0.0, 0.8, 0.0, new int[]{0, 1, 1});
                }
            }
        }
    }

    static {
        animHandler.addAnim("tektopia", "villager_summon", "cleric_m", false);
        animHandler.addAnim("tektopia", "villager_cast_bless", "cleric_m", false);
        EntityVillagerTek.setupAnimations(animHandler, "cleric_m");
    }
}


