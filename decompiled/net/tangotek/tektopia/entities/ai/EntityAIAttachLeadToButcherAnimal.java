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
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIAttachLeadToAnimal;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureRancherPen;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIAttachLeadToButcherAnimal
extends EntityAIAttachLeadToAnimal {
    public EntityAIAttachLeadToButcherAnimal(EntityVillagerTek v, Predicate<EntityVillagerTek> shouldPred) {
        super(v, shouldPred);
    }

    @Override
    public boolean func_75250_a() {
        VillageStructure struct;
        return super.func_75250_a() && (struct = this.villager.getVillage().getNearestStructure(VillageStructureType.BUTCHER, this.villager.func_180425_c())) != null && struct.getEntitiesInside(EntityAnimal.class).size() <= 4;
    }

    @Override
    protected EntityLivingBase getFollowTarget() {
        int rnd = this.villager.field_70173_aa % 4;
        switch (rnd) {
            case 0: {
                this.targetAnimal = this.findButcherAnimal(VillageStructureType.COW_PEN, "retrieve_cows");
                break;
            }
            case 1: {
                this.targetAnimal = this.findButcherAnimal(VillageStructureType.SHEEP_PEN, "retrieve_sheep");
                break;
            }
            case 2: {
                this.targetAnimal = this.findButcherAnimal(VillageStructureType.PIG_PEN, "retrieve_pigs");
                break;
            }
            case 3: {
                this.targetAnimal = this.findButcherAnimal(VillageStructureType.CHICKEN_COOP, "retrieve_chickens");
                break;
            }
            default: {
                this.targetAnimal = null;
            }
        }
        return this.targetAnimal;
    }

    @Override
    protected void attachLeash() {
        super.attachLeash();
    }

    private EntityAnimal findButcherAnimal(VillageStructureType structType, String aiFilter) {
        if (this.villager.isAIFilterEnabled(aiFilter)) {
            List<VillageStructure> structures = this.villager.getVillage().getStructures(structType);
            Collections.shuffle(structures);
            for (VillageStructure struct : structures) {
                VillageStructureRancherPen rancherPen = (VillageStructureRancherPen)struct;
                List animalList = rancherPen.getEntitiesInside(rancherPen.getAnimalClass());
                if (!rancherPen.isPenFull(0.75f)) continue;
                Collections.shuffle(animalList);
                EntityAnimal villagerAnimal = null;
                for (EntityAnimal animal : animalList) {
                    if (animal.func_70631_g_()) continue;
                    if (!ModEntities.isTaggedEntity((Entity)animal, EntityTagType.VILLAGER)) {
                        return animal;
                    }
                    if (villagerAnimal != null) continue;
                    villagerAnimal = animal;
                }
                if (villagerAnimal == null) continue;
                return villagerAnimal;
            }
        }
        return null;
    }

    @Override
    protected void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }
}

