/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIGenericMove;
import net.tangotek.tektopia.entities.ai.EntityAIReadBook;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityNitwit
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityNitwit.class);

    public EntityNitwit(World worldIn) {
        super(worldIn, ProfessionType.NITWIT, VillagerRole.VILLAGER.value);
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void setupServerJobs() {
        super.setupServerJobs();
        this.addJob(new TickJob(1200, 0, true, () -> {
            if (!this.isSleeping()) {
                this.modifyHappy(-1);
            }
        }));
    }

    @Override
    public void modifyHappy(int delta) {
        if (delta > 0) {
            super.modifyHappy(Math.max(delta / 3, 1));
        } else {
            super.modifyHappy(delta);
        }
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIGenericMove(this, p -> p.isWorkTime() && p.hasVillage(), v -> this.village.getLastVillagerPos(), EntityVillagerTek.MovementMode.WALK, null, null));
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
    }

    @Override
    protected void addTask(int priority, EntityAIBase task) {
        if (task instanceof EntityAIReadBook) {
            return;
        }
        super.addTask(priority, task);
    }

    @Override
    protected void applySkillsTo(EntityVillagerTek target) {
        super.applySkillsTo(target);
        int intel = this.getIntelligence();
        int jumpStartSkill = intel / 2 + this.func_70681_au().nextInt(intel) / 2;
        if (jumpStartSkill > target.getSkill(target.getProfessionType())) {
            target.setSkill(target.getProfessionType(), jumpStartSkill);
        }
        this.debugOut("Nitwit converted to " + target.getProfessionType().name + " with " + jumpStartSkill + " intelligence");
    }

    public void func_70071_h_() {
        super.func_70071_h_();
    }

    static {
        EntityVillagerTek.setupAnimations(animHandler, "nitwit_m");
    }
}

