package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;

public class TekLumberjackEntity extends TekWorkerEntity {
    public TekLumberjackEntity(EntityType<? extends TekLumberjackEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.LUMBERJACK);
    }
}
