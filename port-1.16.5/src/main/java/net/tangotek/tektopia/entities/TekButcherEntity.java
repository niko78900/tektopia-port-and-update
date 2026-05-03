package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;

public class TekButcherEntity extends TekWorkerEntity {
    public TekButcherEntity(EntityType<? extends TekButcherEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.BUTCHER);
    }
}
