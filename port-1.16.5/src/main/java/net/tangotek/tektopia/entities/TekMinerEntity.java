package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class TekMinerEntity extends TekWorkerEntity {
    public TekMinerEntity(EntityType<? extends TekMinerEntity> type, World level) {
        super(type, level);
    }
}
