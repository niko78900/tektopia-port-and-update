package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class TekNomadEntity extends TekWorkerEntity {
    public TekNomadEntity(EntityType<? extends TekNomadEntity> type, World level) {
        super(type, level);
    }
}
