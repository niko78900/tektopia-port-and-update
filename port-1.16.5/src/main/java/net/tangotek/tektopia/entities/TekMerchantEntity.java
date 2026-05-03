package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;

public class TekMerchantEntity extends TekWorkerEntity {
    public TekMerchantEntity(EntityType<? extends TekMerchantEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.MERCHANT);
    }
}
