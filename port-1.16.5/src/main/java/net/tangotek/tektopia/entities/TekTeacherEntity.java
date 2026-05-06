package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.tangotek.tektopia.common.ProfessionType;

public class TekTeacherEntity extends TekWorkerEntity {
    public TekTeacherEntity(EntityType<? extends TekTeacherEntity> type, World level) {
        super(type, level);
        this.setInitialProfession(ProfessionType.TEACHER);
    }
}
