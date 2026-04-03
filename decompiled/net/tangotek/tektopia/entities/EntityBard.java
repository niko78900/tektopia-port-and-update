/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIPerformTavern;
import net.tangotek.tektopia.entities.ai.EntityAIPerformWander;
import net.tangotek.tektopia.entities.ai.EntityAIWanderStructure;

public class EntityBard
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityBard.class);
    private static final DataParameter<Byte> PERFORMANCE = EntityDataManager.func_187226_a(EntityBard.class, (DataSerializer)DataSerializers.field_187191_a);
    private static final DataParameter<Boolean> PERFORM_WANDER = EntityDataManager.func_187226_a(EntityBard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PERFORM_TAVERN = EntityDataManager.func_187226_a(EntityBard.class, (DataSerializer)DataSerializers.field_187198_h);
    private int lastPerformanceTick = 0;

    public EntityBard(World worldIn) {
        super(worldIn, ProfessionType.BARD, VillagerRole.VILLAGER.value);
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.registerAIFilter("perform_wander", PERFORM_WANDER);
        this.registerAIFilter("perform_tavern", PERFORM_TAVERN);
        this.field_70180_af.func_187214_a(PERFORMANCE, (Object)0);
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIPerformTavern(this));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIPerformWander(this));
    }

    @Override
    protected void addTask(int priority, EntityAIBase task) {
        if (task instanceof EntityAIWanderStructure) {
            return;
        }
        super.addTask(priority, task);
    }

    public boolean isPerforming() {
        return (Byte)this.field_70180_af.func_187225_a(PERFORMANCE) != 0;
    }

    public ModSoundEvents.Performance getPerformance() {
        Byte perf = (Byte)this.field_70180_af.func_187225_a(PERFORMANCE);
        return ModSoundEvents.Performance.valueOf(perf);
    }

    public void setPerformance(ModSoundEvents.Performance perf) {
        if (perf == null) {
            this.field_70180_af.func_187227_b(PERFORMANCE, (Object)0);
            this.lastPerformanceTick = this.field_70173_aa;
        } else {
            this.field_70180_af.func_187227_b(PERFORMANCE, (Object)perf.id);
        }
    }

    @Override
    protected boolean wantsTavern() {
        return this.hasTavern();
    }

    public int timeSincePerformance() {
        return this.field_70173_aa - this.lastPerformanceTick;
    }

    public void func_70071_h_() {
        super.func_70071_h_();
    }

    static {
        animHandler.addAnim("tektopia", "villager_flute_1", "bard_m", true);
        EntityVillagerTek.setupAnimations(animHandler, "bard_m");
    }
}

