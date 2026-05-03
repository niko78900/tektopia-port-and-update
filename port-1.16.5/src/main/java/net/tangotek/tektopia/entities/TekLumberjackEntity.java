package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class TekLumberjackEntity extends TekWorkerEntity {
    public TekLumberjackEntity(EntityType<? extends TekLumberjackEntity> type, World level) {
        super(type, level);
    }
}
