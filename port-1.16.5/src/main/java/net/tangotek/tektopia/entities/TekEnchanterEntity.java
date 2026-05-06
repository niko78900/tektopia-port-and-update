package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;

public class TekEnchanterEntity extends TekWorkerEntity {
    public TekEnchanterEntity(EntityType<? extends TekEnchanterEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.ENCHANTER);
    }
}
