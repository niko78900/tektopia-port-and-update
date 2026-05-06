package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;

public class TekBardEntity extends TekWorkerEntity {
    public TekBardEntity(EntityType<? extends TekBardEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.BARD);
    }
}
