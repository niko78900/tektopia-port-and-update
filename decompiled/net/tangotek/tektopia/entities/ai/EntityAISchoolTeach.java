/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityChild;
import net.tangotek.tektopia.entities.EntityTeacher;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureSchool;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityAISchoolTeach
extends EntityAIMoveToBlock {
    private VillageStructureSchool school = null;
    private int idleTime = 0;
    private boolean gestured = false;
    private EntityChild watchStudent = null;
    protected final EntityTeacher teacher;

    public EntityAISchoolTeach(EntityTeacher v) {
        super(v);
        this.teacher = v;
    }

    @Override
    public boolean func_75250_a() {
        if (this.teacher.isAITick("teach_school") && this.teacher.hasVillage()) {
            if (EntityTeacher.isSchoolTime(this.teacher.field_70170_p)) {
                return super.func_75250_a();
            }
        }
        return false;
    }

    @Override
    public boolean func_75253_b() {
        if (!this.teacher.hasVillage() || !this.teacher.getVillage().isStructureValid(this.school)) {
            return false;
        }
        if (!EntityTeacher.isSchoolTime(this.teacher.field_70170_p)) {
            return false;
        }
        if (this.idleTime > 0) {
            return true;
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        this.teacher.setMovementMode(this.teacher.getDefaultMovement());
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
        return super.isNearWalkPos();
    }

    @Override
    protected BlockPos getDestinationBlock() {
        VillageStructure struct = this.teacher.getVillage().getNearestStructure(VillageStructureType.SCHOOL, this.teacher.func_180425_c());
        if (struct != null) {
            this.school = (VillageStructureSchool)struct;
            BlockPos pos = this.school.getRandomFloorTile();
            if (pos != null) {
                return pos;
            }
        }
        return null;
    }

    @Override
    public void func_75246_d() {
        super.func_75246_d();
        if (this.hasArrived() && this.watchStudent != null && this.watchStudent.func_70089_S()) {
            this.teacher.func_70625_a((Entity)this.watchStudent, 60.0f, 40.0f);
        }
        if (this.idleTime > 0) {
            --this.idleTime;
        }
        if (this.idleTime > 240 && !this.gestured && this.teacher.func_70681_au().nextInt(150) == 0) {
            List<ItemStack> bookItems;
            this.gestured = true;
            int studentsInside = this.school.getEntitiesInside(EntityChild.class).size();
            if (studentsInside > 2 && !(bookItems = this.teacher.getInventory().removeItems(p -> p.func_77973_b() == Items.field_151122_aG && !p.func_77948_v(), 1)).isEmpty()) {
                int maxGain = 2;
                if (ModItems.isTaggedItem(bookItems.get(0), ItemTagType.VILLAGER)) {
                    maxGain = 1;
                }
                int maxIntGain = maxGain;
                this.teacher.playServerAnimation("villager_teach");
                this.teacher.addJob(new TickJob(8, 0, false, () -> this.teacher.equipActionItem(new ItemStack(Items.field_151122_aG))));
                this.teacher.addJob(new TickJob(50, 0, false, () -> this.teachAll(maxIntGain)));
                this.teacher.addJob(new TickJob(90, 0, false, () -> this.teacher.unequipActionItem()));
                this.teacher.addJob(new TickJob(100, 0, false, () -> this.teacher.stopServerAnimation("villager_teach")));
            }
            if (studentsInside > 0) {
                this.teacher.tryAddSkill(ProfessionType.TEACHER, 4);
            } else {
                this.teacher.throttledSadness(-2);
            }
        }
    }

    private void teachAll(int max) {
        List<EntityChild> students = this.school.getEntitiesInside(EntityChild.class);
        for (EntityChild child : students) {
            int intGain = this.teacher.func_70681_au().nextInt(this.teacher.getSkillLerp(ProfessionType.TEACHER, 1, max));
            child.addIntelligence(intGain);
        }
        this.teacher.tryAddSkill(ProfessionType.TEACHER, 2);
    }

    @Override
    protected void onArrival() {
        this.idleTime = 200 + this.teacher.func_70681_au().nextInt(200);
        this.teacher.modifyHunger(-1);
        List<EntityChild> students = this.school.getEntitiesInside(EntityChild.class);
        if (!students.isEmpty()) {
            this.watchStudent = students.get(this.teacher.func_70681_au().nextInt(students.size()));
        }
        super.onArrival();
    }

    @Override
    public void func_75251_c() {
        this.idleTime = 0;
        this.watchStudent = null;
        this.gestured = false;
        super.func_75251_c();
    }
}

