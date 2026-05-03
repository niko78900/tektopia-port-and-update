package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;
import net.tangotek.tektopia.common.TekWorkerStatus;

public class TekTradesmanEntity extends TekWorkerEntity {
    public TekTradesmanEntity(EntityType<? extends TekTradesmanEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.TRADESMAN);
        this.setWorkerStatus(TekWorkerStatus.VENDING);
    }
}
