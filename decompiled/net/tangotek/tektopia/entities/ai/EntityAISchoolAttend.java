/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import java.util.function.Predicate;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityTeacher;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureSchool;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityAISchoolAttend
extends EntityAIMoveToBlock {
    private BlockPos chairPos;
    private VillageStructureSchool school;
    private int learnTime = 0;
    protected final EntityVillagerTek villager;
    private final Predicate<EntityVillagerTek> shouldPred;

    public EntityAISchoolAttend(EntityVillagerTek v, Predicate<EntityVillagerTek> shouldPred) {
        super(v);
        this.villager = v;
        this.shouldPred = shouldPred;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick("attend_school") && this.villager.hasVillage() && this.shouldPred.test(this.villager) && this.villager.isWorkTime() && super.func_75250_a() && this.school != null) {
            if (this.school.hasTeacherInside()) {
                return true;
            }
            this.villager.setThought(EntityVillagerTek.VillagerThought.TEACHER);
        }
        return false;
    }

    @Override
    public boolean func_75253_b() {
        if (this.hasArrived() && this.chairPos != null && this.school.isValid() && this.school.hasTeacherInside() && EntityTeacher.isSchoolTime(this.villager.field_70170_p)) {
            return true;
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        if (this.villager.func_70681_au().nextInt(5) == 0) {
            this.villager.setMovementMode(EntityVillagerTek.MovementMode.SULK);
            this.villager.modifyHappy(-10);
        } else {
            this.villager.setMovementMode(this.villager.getDefaultMovement());
        }
    }

    @Override
    public void func_75249_e() {
        super.func_75249_e();
    }

    @Override
    protected BlockPos findWalkPos() {
        return this.destinationPos;
    }

    @Override
    protected boolean isNearWalkPos() {
        if (this.chairPos != null) {
            return this.chairPos != null && this.chairPos.func_177951_i((Vec3i)this.villager.func_180425_c()) <= 1.0;
        }
        return super.isNearWalkPos();
    }

    @Override
    protected BlockPos getDestinationBlock() {
        VillageStructure struct = this.villager.getVillage().getNearestStructure(VillageStructureType.SCHOOL, this.villager.func_180425_c());
        if (struct != null) {
            this.school = (VillageStructureSchool)struct;
            if (this.school.hasTeacherInside()) {
                this.chairPos = this.school.tryVillagerSit(this.villager);
                if (this.chairPos != null) {
                    return this.chairPos;
                }
            }
        }
        return null;
    }

    @Override
    public void func_75246_d() {
        if (this.learnTime > 0) {
            --this.learnTime;
            if (this.learnTime % 10 == 0) {
                this.moveToSitPos();
            }
            if (this.learnTime == 90) {
                EntityTeacher teacher = this.school.getTeacherInside();
                if (teacher != null) {
                    int schoolEvent = this.villager.func_70681_au().nextInt(teacher.getSkillLerp(ProfessionType.TEACHER, 18, 9));
                    if (schoolEvent <= 2) {
                        this.villager.addIntelligence(1);
                    } else {
                        if (schoolEvent <= 7) {
                            this.villager.playServerAnimation("villager_sit_raise");
                            this.villager.addJob(new TickJob(80, 0, false, () -> this.villager.playServerAnimation("villager_sit")));
                            teacher.throttledSadness(-1);
                        }
                        teacher.tryAddSkill(ProfessionType.TEACHER, 50);
                    }
                }
            } else if (this.learnTime <= 0) {
                this.startLearn();
            }
        }
        super.func_75246_d();
    }

    @Override
    protected void onArrival() {
        if (this.chairPos != null) {
            this.villager.setMovementMode(this.villager.getDefaultMovement());
            this.startSit();
            this.startLearn();
        }
        super.onArrival();
    }

    private void startLearn() {
        this.learnTime = 150 + this.villager.func_70681_au().nextInt(150);
        if (this.school.hasTeacherInside()) {
            this.learnTime -= this.school.getTeacherInside().getSkill(ProfessionType.TEACHER);
        }
    }

    private EnumFacing getChairFacing() {
        if (this.chairPos != null && this.villager.field_70170_p.func_175667_e(this.chairPos)) {
            IBlockState state = this.villager.field_70170_p.func_180495_p(this.chairPos);
            EnumFacing enumfacing = state.func_177230_c() instanceof BlockHorizontal ? ((EnumFacing)state.func_177229_b((IProperty)BlockHorizontal.field_185512_D)).func_176734_d() : null;
            return enumfacing;
        }
        return null;
    }

    private int getChairAxis() {
        EnumFacing facing = this.getChairFacing();
        if (facing != null) {
            return facing.func_176736_b();
        }
        return -1;
    }

    private void startSit() {
        int chairAxis = this.getChairAxis();
        if (chairAxis >= 0) {
            this.moveToSitPos();
            this.villager.onStartSit(chairAxis);
        }
    }

    private Vec3d getSitPos() {
        return new Vec3d((double)this.destinationPos.func_177958_n() + 0.5, (double)this.destinationPos.func_177956_o() + this.villager.getSitOffset(), (double)this.destinationPos.func_177952_p() + 0.5);
    }

    private void moveTo(Vec3d pos) {
        this.villager.func_70012_b(pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, this.villager.field_70177_z, this.villager.field_70125_A);
        this.villager.field_70159_w = 0.0;
        this.villager.field_70181_x = 0.0;
        this.villager.field_70179_y = 0.0;
    }

    private void moveToSitPos() {
        Vec3d sitPos = this.getSitPos();
        if (this.villager.func_174791_d().func_186679_c(sitPos.field_72450_a, sitPos.field_72448_b, sitPos.field_72449_c) > 0.05) {
            this.moveTo(sitPos);
        }
    }

    @Override
    public void func_75251_c() {
        if (this.chairPos != null && this.school != null && this.school.isValid()) {
            this.school.vacateSpecialBlock(this.chairPos);
            this.chairPos = null;
        }
        this.villager.setMovementMode(this.villager.getDefaultMovement());
        this.villager.onStopSit();
        this.learnTime = 0;
        super.func_75251_c();
    }
}

