package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class TekChefEntity extends TekWorkerEntity {
    public TekChefEntity(EntityType<? extends TekChefEntity> type, World level) {
        super(type, level);
    }
}
