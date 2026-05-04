package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;

public class TekNitwitEntity extends TekWorkerEntity {
    public TekNitwitEntity(EntityType<? extends TekNitwitEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.NITWIT);
    }
}
