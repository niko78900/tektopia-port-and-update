/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityChild;
import net.tangotek.tektopia.entities.EntityTeacher;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;

public class VillageStructureSchool
extends VillageStructure {
    protected EntityTeacher teacherInside = null;

    protected VillageStructureSchool(World world, Village v, EntityItemFrame itemFrame) {
        super(world, v, itemFrame, VillageStructureType.SCHOOL, "School");
    }

    @Override
    protected void setupServerJobs() {
        this.addJob(new TickJob(30, 50, true, () -> {
            List<EntityTeacher> teacherList = this.getEntitiesInside(EntityTeacher.class);
            this.teacherInside = teacherList.isEmpty() ? null : teacherList.get(0);
        }));
        super.setupServerJobs();
    }

    @Override
    protected void scanSpecialBlock(BlockPos pos, Block block) {
        if (block == Blocks.field_150462_ai) {
            this.addSpecialBlock(Blocks.field_150462_ai, pos);
        }
        super.scanSpecialBlock(pos, block);
    }

    public boolean hasTeacherInside() {
        return this.teacherInside != null;
    }

    public EntityTeacher getTeacherInside() {
        return this.teacherInside;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public int getSitTime(EntityVillagerTek villager) {
        return Integer.MAX_VALUE;
    }

    @Override
    protected boolean shouldVillagerSit(EntityVillagerTek villager) {
        return villager instanceof EntityChild;
    }
}

