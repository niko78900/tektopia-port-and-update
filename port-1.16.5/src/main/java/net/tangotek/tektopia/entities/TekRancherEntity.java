package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;

public class TekRancherEntity extends TekWorkerEntity {
    public TekRancherEntity(EntityType<? extends TekRancherEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.RANCHER);
    }
}
