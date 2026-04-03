/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityNitwit;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIGenericMove;
import net.tangotek.tektopia.entities.ai.EntityAIPlayTag;
import net.tangotek.tektopia.entities.ai.EntityAIReadBook;
import net.tangotek.tektopia.entities.ai.EntityAISchoolAttend;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityChild
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityChild.class);
    private static final DataParameter<Byte> VARIATION = EntityDataManager.func_187226_a(EntityChild.class, (DataSerializer)DataSerializers.field_187191_a);
    private static final DataParameter<Boolean> PLAY_TAG = EntityDataManager.func_187226_a(EntityChild.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> ATTEND_SCHOOL = EntityDataManager.func_187226_a(EntityChild.class, (DataSerializer)DataSerializers.field_187198_h);
    protected EntityChild chasedBy = null;
    protected boolean attendedSchoolToday = false;
    private int daysForAdult;

    public EntityChild(World worldIn) {
        super(worldIn, ProfessionType.CHILD, VillagerRole.VILLAGER.value);
        this.func_70105_a(this.field_70130_N * 0.5f, this.field_70131_O * 0.5f);
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    @Nullable
    public IEntityLivingData func_180482_a(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        this.setVariation((byte)this.field_70146_Z.nextInt(2));
        return super.func_180482_a(difficulty, livingdata);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_187214_a(VARIATION, ((byte)this.field_70146_Z.nextInt(2)));
        this.registerAIFilter("play_tag", PLAY_TAG);
        this.registerAIFilter("attend_school", ATTEND_SCHOOL);
        this.removeAIFilter("visit_tavern");
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.daysForAdult = 4 + this.func_70681_au().nextInt(2);
        this.addTask(50, new EntityAISchoolAttend(this, p -> p.isWorkTime() && this.needsSchoolToday()));
        this.addTask(50, new EntityAIPlayTag(this));
        this.addTask(50, new EntityAIGenericMove(this, p -> p.isWorkTime() && p.hasVillage() && p.func_70681_au().nextInt(2) == 0, v -> this.village.getLastVillagerPos(), EntityVillagerTek.MovementMode.SKIP, null, null));
        this.addTask(50, new EntityAIGenericMove(this, p -> p.isWorkTime() && p.hasVillage(), v -> this.village.getLastVillagerPos(), EntityVillagerTek.MovementMode.WALK, null, null));
    }

    @Override
    protected void onNewDay() {
        super.onNewDay();
        this.checkGrowAdult();
    }

    @Override
    public void attachToVillage(Village v) {
        super.attachToVillage(v);
        this.sleepOffset = this.genOffset(400) - 1200;
    }

    @Override
    protected void setupServerJobs() {
        super.setupServerJobs();
    }

    @Override
    protected void addTask(int priority, EntityAIBase task) {
        if (task instanceof EntityAIReadBook) {
            return;
        }
        super.addTask(priority, task);
    }

    protected void setVariation(Byte v) {
        this.field_70180_af.func_187227_b(VARIATION, v);
    }

    public Byte getVariation() {
        return (byte)((Byte)this.field_70180_af.func_187225_a(VARIATION));
    }

    @Override
    public void onStartSleep(int sleepAxis) {
        super.onStartSleep(sleepAxis);
        this.attendedSchoolToday = false;
    }

    @Override
    public void onStartSit(int sitAxis) {
        VillageStructure curStruct = this.getCurrentStructure();
        if (curStruct != null && curStruct.type == VillageStructureType.SCHOOL) {
            this.attendedSchoolToday = true;
        }
        super.onStartSit(sitAxis);
    }

    @Override
    public double getSitOffset() {
        return 0.24;
    }

    public boolean needsSchoolToday() {
        return !this.attendedSchoolToday;
    }

    public boolean func_70631_g_() {
        return true;
    }

    @Override
    public boolean canConvertProfession(ProfessionType pt) {
        return false;
    }

    public float func_70047_e() {
        return 0.87f;
    }

    @Override
    protected boolean wantsTavern() {
        return false;
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
    }

    @Override
    protected boolean canVillagerPickupItem(ItemStack itemIn) {
        return super.canVillagerPickupItem(itemIn);
    }

    @Override
    protected void cleanUpInventory() {
        super.cleanUpInventory();
    }

    protected void checkGrowAdult() {
        if (this.daysAlive >= this.daysForAdult) {
            EntityNitwit villager = new EntityNitwit(this.field_70170_p);
            villager.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, 0.0f, 0.0f);
            villager.func_180482_a(this.field_70170_p.func_175649_E(this.func_180425_c()), null);
            villager.cloneFrom(this);
            this.field_70170_p.func_72838_d((Entity)villager);
        }
    }

    @Override
    public float func_70689_ay() {
        return super.func_70689_ay() * 0.75f;
    }

    public void setChasedBy(EntityChild child) {
        this.chasedBy = child;
    }

    public EntityChild getChasedBy() {
        return this.chasedBy;
    }

    @Override
    public boolean isFleeFrom(Entity e) {
        return e == this.chasedBy || super.isFleeFrom(e);
    }

    @Override
    public void playSound(SoundEvent soundEvent) {
        this.func_184185_a(soundEvent, this.func_70681_au().nextFloat() * 0.4f + 0.8f, this.func_70681_au().nextFloat() * 0.4f + 1.1f);
    }

    @Override
    public void func_70014_b(NBTTagCompound compound) {
        super.func_70014_b(compound);
    }

    @Override
    public void func_70037_a(NBTTagCompound compound) {
        super.func_70037_a(compound);
    }

    static {
        animHandler.addAnim("tektopia", "villager_skip", "child_m", true);
        animHandler.addAnim("tektopia", "villager_sit_raise", "child_m", false);
        EntityVillagerTek.setupAnimations(animHandler, "child_m");
    }
}


