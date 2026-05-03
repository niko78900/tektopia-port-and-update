package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class TekMerchantEntity extends TekWorkerEntity {
    public TekMerchantEntity(EntityType<? extends TekMerchantEntity> type, World level) {
        super(type, level);
    }
}
