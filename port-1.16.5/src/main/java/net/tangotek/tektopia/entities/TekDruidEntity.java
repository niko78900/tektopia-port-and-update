package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;

public class TekDruidEntity extends TekWorkerEntity {
    public TekDruidEntity(EntityType<? extends TekDruidEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.DRUID);
    }
}
