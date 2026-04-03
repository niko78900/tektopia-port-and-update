/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAITarget
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIKillInStructure
extends EntityAITarget {
    private final EntityVillagerTek villager;
    private final VillageStructureType structureType;
    private final Class<? extends EntityLivingBase> clazz;
    private EntityLivingBase targetEntity;
    private final Predicate<EntityVillagerTek> shouldPred;
    private final Predicate<EntityLivingBase> targetPred;

    public EntityAIKillInStructure(EntityVillagerTek villager, VillageStructureType structureType, Class<? extends EntityLivingBase> clazz, Predicate<EntityLivingBase> isTargetPred, Predicate<EntityVillagerTek> shouldPred) {
        super((EntityCreature)villager, false, false);
        this.villager = villager;
        this.structureType = structureType;
        this.clazz = clazz;
        this.func_75248_a(1);
        this.shouldPred = shouldPred;
        this.targetPred = isTargetPred;
    }

    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.shouldPred.test(this.villager)) {
            this.targetEntity = this.findTarget();
            return this.targetEntity != null;
        }
        return false;
    }

    private EntityLivingBase findTarget() {
        List<VillageStructure> structures = this.villager.getVillage().getStructures(this.structureType);
        for (VillageStructure struct : structures) {
            List<? extends EntityLivingBase> entList = struct.getEntitiesInside(this.clazz);
            entList.removeIf(this.targetPred.negate());
            for (EntityLivingBase entityLivingBase : entList) {
                if (entityLivingBase.func_70631_g_() || !this.func_75296_a(entityLivingBase, false)) continue;
                return entityLivingBase;
            }
        }
        return null;
    }

    public boolean func_75253_b() {
        boolean shouldContinue = super.func_75253_b();
        if (!shouldContinue && this.villager.hasVillage()) {
            this.targetEntity = this.findTarget();
            if (this.targetEntity != null) {
                this.field_75299_d.func_70624_b(this.targetEntity);
                shouldContinue = true;
            }
        }
        return shouldContinue;
    }

    public void func_75249_e() {
        this.villager.func_70624_b(this.targetEntity);
        this.villager.throttledSadness(-5);
        super.func_75249_e();
    }

    public void func_75251_c() {
        this.targetEntity = null;
        super.func_75251_c();
    }
}

