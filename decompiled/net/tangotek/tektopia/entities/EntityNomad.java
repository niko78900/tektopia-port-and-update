/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.google.common.base.Predicate;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIGenericMove;
import net.tangotek.tektopia.entities.ai.EntityAIReadBook;
import net.tangotek.tektopia.entities.ai.EntityAIWanderStructure;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityNomad
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityNomad.class);
    private BlockPos firstCheck;
    private BlockPos spawnPos = null;

    public EntityNomad(World worldIn) {
        super(worldIn, ProfessionType.NOMAD, VillagerRole.VISITOR.value);
    }

    @Override
    protected void setupServerJobs() {
        super.setupServerJobs();
        this.addJob(new TickJob(20, 0, false, () -> this.postSetup()));
        this.addJob(new TickJob(100, 0, false, () -> this.prepStuck()));
        this.addJob(new TickJob(400, 0, false, () -> this.checkStuck()));
        this.addJob(new TickJob(50, 0, true, () -> {
            if (this.isSleepingTime()) {
                this.func_70106_y();
            }
        }));
        this.addJob(new TickJob(300, 100, true, () -> {
            if (!this.hasVillage() || !this.getVillage().isValid()) {
                this.func_70106_y();
            }
        }));
    }

    private void prepStuck() {
        this.firstCheck = this.func_180425_c();
    }

    private void checkStuck() {
        if (this.hasVillage() && this.firstCheck.func_177951_i((Vec3i)this.func_180425_c()) < 20.0) {
            this.village.sendChatMessage("Nomad failed to find a way to the village.");
            this.func_70106_y();
        }
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.addTask(50, new EntityAIGenericMove(this, p -> Village.isNightTime(this.field_70170_p), v -> this.spawnPos, EntityVillagerTek.MovementMode.WALK, null, () -> this.func_70106_y()));
        this.addTask(50, new EntityAIGenericMove(this, p -> !Village.isNightTime(this.field_70170_p) && p.hasVillage(), v -> this.village.getLastVillagerPos(), EntityVillagerTek.MovementMode.WALK, null, null));
    }

    @Override
    protected void initEntityAIBase() {
    }

    @Override
    @Nullable
    public IEntityLivingData func_180482_a(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        int skillCount = this.func_70681_au().nextInt(2) + 1;
        int profCount = ProfessionType.values().length;
        for (int i = 0; i < skillCount; ++i) {
            int newSkill;
            ProfessionType prof = ProfessionType.values()[this.func_70681_au().nextInt(profCount)];
            if (!prof.canCopy) continue;
            for (newSkill = this.getBaseSkill(prof) + 3 + this.func_70681_au().nextInt(12); this.func_70681_au().nextInt(7) <= 3 && newSkill < 25; ++newSkill) {
            }
            this.setSkill(prof, newSkill);
        }
        return super.func_180482_a(difficulty, livingdata);
    }

    @Override
    public Predicate<Entity> isHostile() {
        return e -> false;
    }

    @Override
    public boolean isFleeFrom(Entity e) {
        return false;
    }

    @Override
    public float func_70689_ay() {
        return super.func_70689_ay() * 0.9f;
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
    }

    @Override
    protected void addTask(int priority, EntityAIBase task) {
        if (task instanceof EntityAIWanderStructure && priority <= 100) {
            return;
        }
        if (task instanceof EntityAIReadBook) {
            return;
        }
        super.addTask(priority, task);
    }

    private void postSetup() {
        this.spawnPos = this.func_180425_c();
    }

    @Override
    public void addVillagerPosition() {
    }

    @Override
    protected void bedCheck() {
    }

    @Override
    public void func_70014_b(NBTTagCompound compound) {
        super.func_70014_b(compound);
        if (this.spawnPos == null) {
            this.spawnPos = BlockPos.field_177992_a;
        }
        this.writeBlockPosNBT(compound, "spawnPos", this.spawnPos);
    }

    @Override
    public void func_70037_a(NBTTagCompound compound) {
        super.func_70037_a(compound);
        this.spawnPos = this.readBlockPosNBT(compound, "spawnPos");
    }

    static {
        EntityVillagerTek.setupAnimations(animHandler, "nomad_m");
    }
}

