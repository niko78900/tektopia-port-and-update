package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;

public class TekChefEntity extends TekWorkerEntity {
    public TekChefEntity(EntityType<? extends TekChefEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.CHEF);
    }
}
