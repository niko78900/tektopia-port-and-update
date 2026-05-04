package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;

public class TekArchitectEntity extends TekWorkerEntity {
    public TekArchitectEntity(EntityType<? extends TekArchitectEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.ARCHITECT);
        this.setWorkerStatus(net.tangotek.tektopia.common.TekWorkerStatus.VENDING);
    }
}
