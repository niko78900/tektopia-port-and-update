/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAILeadAnimalToStructure;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureRancherPen;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIReturnLostAnimal
extends EntityAILeadAnimalToStructure {
    public EntityAIReturnLostAnimal(EntityVillagerTek v) {
        super(v, VillageStructureType.HOME2, null);
    }

    @Override
    protected VillageStructure getDestinationStructure() {
        return EntityAIReturnLostAnimal.getDestinationStructure(this.villager.getVillage(), this.villager.getLeadAnimal(), this.villager.func_180425_c());
    }

    public static VillageStructure getDestinationStructure(Village village, EntityAnimal animal, BlockPos pos) {
        VillageStructure destStructure = null;
        if (village != null && animal != null) {
            destStructure = EntityAIReturnLostAnimal.getAnimalHome(village, animal, VillageStructureType.COW_PEN, pos);
            if (destStructure == null) {
                destStructure = EntityAIReturnLostAnimal.getAnimalHome(village, animal, VillageStructureType.SHEEP_PEN, pos);
            }
            if (destStructure == null) {
                destStructure = EntityAIReturnLostAnimal.getAnimalHome(village, animal, VillageStructureType.PIG_PEN, pos);
            }
            if (destStructure == null) {
                destStructure = EntityAIReturnLostAnimal.getAnimalHome(village, animal, VillageStructureType.CHICKEN_COOP, pos);
            }
        }
        return destStructure;
    }

    private static VillageStructure getAnimalHome(Village village, EntityAnimal animal, VillageStructureType structureType, BlockPos pos) {
        Double min = Double.MAX_VALUE;
        VillageStructureRancherPen closestPen = null;
        List<VillageStructure> structList = village.getStructures(structureType);
        for (VillageStructure struct : structList) {
            Double dist;
            VillageStructureRancherPen pen;
            if (!(struct instanceof VillageStructureRancherPen) || !(pen = (VillageStructureRancherPen)struct).isAnimalHome(animal) || !((dist = Double.valueOf(pen.getDoor().func_177951_i((Vec3i)pos))) < min)) continue;
            min = dist;
            closestPen = pen;
        }
        return closestPen;
    }
}

