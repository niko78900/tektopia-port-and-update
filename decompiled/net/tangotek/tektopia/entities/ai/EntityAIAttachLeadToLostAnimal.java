/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.passive.EntityAnimal
 */
package net.tangotek.tektopia.entities.ai;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.tangotek.tektopia.EntityTagType;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.entities.EntityMerchant;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIAttachLeadToAnimal;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureRancherPen;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIAttachLeadToLostAnimal
extends EntityAIAttachLeadToAnimal {
    private int animalCheck;

    public EntityAIAttachLeadToLostAnimal(EntityVillagerTek v, Predicate<EntityVillagerTek> shouldPred) {
        super(v, shouldPred);
    }

    @Override
    protected EntityLivingBase getFollowTarget() {
        this.targetAnimal = null;
        if (this.villager.isAIFilterEnabled("return_lost_animals")) {
            ++this.animalCheck;
            switch (this.animalCheck) {
                case 1: {
                    this.targetAnimal = this.findLostAnimal(VillageStructureType.COW_PEN);
                    break;
                }
                case 3: {
                    this.targetAnimal = this.findLostAnimal(VillageStructureType.SHEEP_PEN);
                    break;
                }
                case 5: {
                    this.targetAnimal = this.findLostAnimal(VillageStructureType.PIG_PEN);
                    break;
                }
                case 7: {
                    this.targetAnimal = this.findLostAnimal(VillageStructureType.CHICKEN_COOP);
                    break;
                }
                default: {
                    this.targetAnimal = null;
                }
            }
            if (this.animalCheck > 20) {
                this.animalCheck = 0;
            }
        }
        return this.targetAnimal;
    }

    private EntityAnimal findLostAnimal(VillageStructureType structType) {
        List<VillageStructure> structList = this.villager.getVillage().getStructures(structType);
        if (!structList.isEmpty() && structList.get(0) instanceof VillageStructureRancherPen) {
            VillageStructureRancherPen rancherPen = (VillageStructureRancherPen)structList.get(0);
            List animalList = this.villager.field_70170_p.func_72872_a(rancherPen.getAnimalClass(), this.villager.func_174813_aQ().func_72314_b(40.0, 6.0, 40.0));
            Collections.shuffle(animalList);
            for (EntityAnimal animal : animalList) {
                if (animal.func_110166_bE() != null && !(animal.func_110166_bE() instanceof EntityMerchant) || ModEntities.isTaggedEntity((Entity)animal, EntityTagType.BUTCHERED)) continue;
                boolean isLost = true;
                for (VillageStructure struct : structList) {
                    if (!struct.isBlockNear(animal.func_180425_c(), 1.5)) continue;
                    isLost = false;
                }
                if (!isLost) continue;
                return animal;
            }
        }
        return null;
    }

    @Override
    protected void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }
}

