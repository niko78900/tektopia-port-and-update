/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.entity.ai.EntityAITasks$EntityAITaskEntry
 *  net.minecraft.entity.ai.attributes.IAttribute
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.ai.attributes.RangedAttribute
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.monster.EntityEvoker
 *  net.minecraft.entity.monster.EntityPigZombie
 *  net.minecraft.entity.monster.EntityVex
 *  net.minecraft.entity.monster.EntityVindicator
 *  net.minecraft.entity.monster.EntityWitherSkeleton
 *  net.minecraft.entity.monster.EntityZombie
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.text.translation.I18n
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ITeleporter
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.network.NetworkRegistry$TargetPoint
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.entities;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.EntityTagType;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ModPotions;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.client.ParticleThought;
import net.tangotek.tektopia.entities.EntityChild;
import net.tangotek.tektopia.entities.EntityNecromancer;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.entities.ai.EntityAIDeliverToStorage2;
import net.tangotek.tektopia.entities.ai.EntityAIEatFood;
import net.tangotek.tektopia.entities.ai.EntityAIFleeEntity;
import net.tangotek.tektopia.entities.ai.EntityAIGenericMove;
import net.tangotek.tektopia.entities.ai.EntityAIIdleCheck;
import net.tangotek.tektopia.entities.ai.EntityAIOpenGate;
import net.tangotek.tektopia.entities.ai.EntityAIReadBook;
import net.tangotek.tektopia.entities.ai.EntityAIRetrieveFromStorage2;
import net.tangotek.tektopia.entities.ai.EntityAISleep;
import net.tangotek.tektopia.entities.ai.EntityAITavernVisit;
import net.tangotek.tektopia.entities.ai.EntityAIUseDoor;
import net.tangotek.tektopia.entities.ai.EntityAIWanderStructure;
import net.tangotek.tektopia.items.ItemProfessionToken;
import net.tangotek.tektopia.network.PacketVillagerThought;
import net.tangotek.tektopia.storage.ItemDesireSet;
import net.tangotek.tektopia.storage.UpgradeItemDesire;
import net.tangotek.tektopia.storage.VillagerInventory;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureHome;
import net.tangotek.tektopia.structures.VillageStructureSchool;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;

public abstract class EntityVillagerTek
extends EntityVillageNavigator {
    public static final IAttribute MAX_HUNGER = new RangedAttribute((IAttribute)null, "generic.hunger", 100.0, 0.0, 100.0).func_111117_a("Hunger").func_111112_a(true);
    public static final IAttribute MAX_HAPPY = new RangedAttribute((IAttribute)null, "generic.happy", 100.0, 0.0, 100.0).func_111117_a("Happy").func_111112_a(true);
    public static final IAttribute MAX_INTELLIGENCE = new RangedAttribute((IAttribute)null, "generic.intelligence", 100.0, 0.0, 100.0).func_111117_a("Intelligence").func_111112_a(true);
    private static final DataParameter<Integer> HUNGER = EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187192_b);
    private static final DataParameter<Integer> HAPPY = EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187192_b);
    private static final DataParameter<Integer> INTELLIGENCE = EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187192_b);
    private static final DataParameter<Integer> FORCE_AXIS = EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187192_b);
    private static final DataParameter<Boolean> SLEEPING = EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<ItemStack> ACTION_ITEM = EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187196_f);
    private static final DataParameter<Byte> MOVEMENT_MODE = EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187191_a);
    private static final DataParameter<Integer> BLESSED = EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187192_b);
    private static final DataParameter<Boolean> VISIT_TAVERN = EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> READ_BOOK = EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187198_h);
    public static int SLEEP_DURATION = 8000;
    public static int SLEEP_START_TIME = 16000;
    public static int SLEEP_END_TIME = SLEEP_START_TIME + SLEEP_DURATION;
    public static int WORK_START_TIME = 500;
    public static int WORK_END_TIME = 11500;
    private static final boolean ALL_MALES = false;
    private static final boolean ALL_FEMALES = false;
    protected static final int DEFAULT_JOB_PRIORITY = 50;
    private PacketVillagerThought nextThought = null;
    private static int[] recentEatPenalties = new int[]{2, 0, -3, -7, -12, -18};
    private static final Map<ProfessionType, DataParameter<Integer>> SKILLS = new EnumMap<ProfessionType, DataParameter<Integer>>(ProfessionType.class);
    protected ItemDesireSet desireSet;
    protected VillagerInventory villagerInventory;
    protected BlockPos bedPos = null;
    protected BlockPos homeFrame = null;
    protected EntityAnimal leadAnimal = null;
    private MovementMode lastMovementMode;
    private VillageStructure lastCrowdCheck = null;
    protected int sleepOffset = 0;
    protected int wantsLearning = 0;
    protected boolean wantsTavern = false;
    protected int lastSadTick = 0;
    protected int lastSadThrottle = 200;
    private int idle = 0;
    protected int daysAlive = 0;
    private int dayCheckTime = 0;
    private final ProfessionType professionType;
    private Map<String, DataParameter<Boolean>> aiFilters;
    private LinkedList<Integer> recentEats = new LinkedList();

    public EntityVillagerTek(World worldIn, ProfessionType profType, int roleMask) {
        super(worldIn, roleMask);
        this.professionType = profType;
        this.func_98053_h(true);
        this.villagerInventory = new VillagerInventory(this, "Items", false, 27);
        this.func_70105_a(0.6f, 1.95f);
        this.setHunger(this.getMaxHunger());
        this.setHappy(this.getMaxHappy());
        this.setIntelligence(0);
        ModEntities.makeTaggedEntity((Entity)this, EntityTagType.VILLAGER);
        Runnable foodChomp = () -> this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187537_bA, SoundCategory.NEUTRAL, 1.0f, this.field_70146_Z.nextFloat() * 0.2f + 0.9f, false);
        Runnable doneEating = () -> {
            this.unequipActionItem();
            if (this.func_70681_au().nextInt(12) == 0) {
                this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187739_dZ, SoundCategory.NEUTRAL, 1.0f, this.field_70146_Z.nextFloat() * 0.2f + 0.9f, false);
            }
        };
        if (this.field_70170_p.field_72995_K) {
            this.addAnimationTrigger("tektopia:villager_eat", 25, foodChomp);
            this.addAnimationTrigger("tektopia:villager_eat", 50, foodChomp);
            this.addAnimationTrigger("tektopia:villager_eat", 75, foodChomp);
            this.addAnimationTrigger("tektopia:villager_eat", 90, doneEating);
        }
        if (!worldIn.field_72995_K) {
            this.randomizeGoals();
            if (this.professionType != null && this.getBaseSkill(this.professionType) < 1) {
                this.setSkill(this.professionType, 1);
            }
        }
    }

    protected static void setupAnimations(AnimationHandler animHandler, String modelName) {
        EntityVillageNavigator.setupAnimations(animHandler, modelName);
        animHandler.addAnim("tektopia", "villager_eat", modelName, false);
        animHandler.addAnim("tektopia", "villager_sleep", modelName, true);
        animHandler.addAnim("tektopia", "villager_sit", modelName, true);
        animHandler.addAnim("tektopia", "villager_sit_cheer", modelName, false);
        animHandler.addAnim("tektopia", "villager_walk", modelName, true);
        animHandler.addAnim("tektopia", "villager_walk_sad", modelName, true);
        animHandler.addAnim("tektopia", "villager_run", modelName, true);
        animHandler.addAnim("tektopia", "villager_read", modelName, false);
    }

    protected void initEntityAIBase() {
        Function<ItemStack, Integer> bestFood = p -> EntityAIEatFood.getFoodScore(p.func_77973_b(), this);
        this.getDesireSet().addItemDesire(new UpgradeItemDesire("Food", bestFood, 1, 3, 4, p -> p.isHungry() || p.isStoragePriority()));
        this.addTask(50, new EntityAIEatFood(this));
        this.addTask(50, new EntityAIRetrieveFromStorage2(this));
        this.addTask(50, new EntityAISleep(this));
        this.addTask(50, new EntityAIDeliverToStorage2(this));
        this.addTask(50, new EntityAITavernVisit(this, p -> p.wantsTavern() && !p.isWorkTime() && !p.shouldSleep()));
        this.addTask(50, new EntityAIWanderStructure(this, EntityVillagerTek.getVillagerHome(), p -> !p.isWorkTime() && !this.wantsTavern(), 12));
        this.addTask(50, new EntityAIReadBook(this));
        this.addTask(60, new EntityAIGenericMove(this, p -> p.isWorkTime() && p.hasVillage() && p.getIdle() > 100, v -> this.village.getLastVillagerPos(), MovementMode.WALK, null, null));
        this.addTask(150, new EntityAIIdleCheck(this));
    }

    protected void func_184651_r() {
        this.desireSet = new ItemDesireSet();
        this.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.addTask(1, new EntityAIFleeEntity(this, (com.google.common.base.Predicate<Entity>)((com.google.common.base.Predicate)p -> this.isFleeFrom((Entity)p)), 16.0f));
        this.addTask(15, new EntityAIUseDoor((EntityLiving)this, true));
        this.addTask(15, new EntityAIOpenGate(this));
        this.initEntityAIBase();
    }

    @Nullable
    public IEntityLivingData func_180482_a(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        this.func_96094_a(this.generateName());
        double intel = this.func_70681_au().nextGaussian() * 10.0;
        if (intel < 0.0) {
            intel *= 0.3;
        }
        intel = Math.max(intel + 10.0, 2.0);
        this.setIntelligence((int)intel);
        return super.func_180482_a(difficulty, livingdata);
    }

    public String generateName() {
        String nameTytpe = this.isMale() ? "malename" : "femalename";
        String firstSTotal = I18n.func_74838_a((String)("villager." + nameTytpe + ".total"));
        int firstTotal = Integer.parseInt(firstSTotal);
        String lastSTotal = I18n.func_74838_a((String)"villager.lastname.total");
        int lastTotal = Integer.parseInt(lastSTotal);
        String firstName = I18n.func_74838_a((String)("villager." + nameTytpe + "." + this.func_70681_au().nextInt(firstTotal)));
        String lastName = I18n.func_74838_a((String)("villager.lastname." + this.func_70681_au().nextInt(lastTotal)));
        return firstName + " " + lastName;
    }

    public String getLastName() {
        String name = this.func_95999_t();
        String[] splitNames = name.split("\\s+");
        if (splitNames.length >= 2) {
            return splitNames[1];
        }
        return "";
    }

    public String getFirstName() {
        String name = this.func_95999_t();
        String[] splitNames = name.split("\\s+");
        if (splitNames.length >= 2) {
            return splitNames[0];
        }
        return "";
    }

    public String getDebugName() {
        return ((Object)((Object)this)).getClass().getSimpleName() + this.func_145782_y();
    }

    public ProfessionType getProfessionType() {
        return this.professionType;
    }

    protected boolean hasTavern() {
        return this.hasVillage() && this.getVillage().hasStructure(VillageStructureType.TAVERN);
    }

    protected boolean wantsTavern() {
        if (this.getHappy() >= 100 || !this.hasTavern()) {
            return false;
        }
        if (!this.isWorkTime() && (this.getHappy() < 70 || this.wantsTavern)) {
            return true;
        }
        return this.getHappy() < 10;
    }

    protected void addTask(int priority, EntityAIBase task) {
        this.field_70714_bg.func_75776_a(priority, task);
    }

    public boolean isDeliveryTime() {
        return this.isWorkTime();
    }

    @Override
    protected void setupServerJobs() {
        super.setupServerJobs();
        this.addJob(new TickJob(60, 120, true, new CleanUpRunnable(this)));
        this.addJob(new TickJob(23900, 200, true, new GoalRandomizerRunnable(this)));
        this.addJob(new TickJob(80, 10, true, () -> {
            if (this.isStarving()) {
                this.func_70097_a(DamageSource.field_76366_f, 1.0f);
            }
        }));
        this.addJob(new TickJob(30, 30, true, () -> this.scanForEnemies()));
        this.addJob(new TickJob(100, 200, true, () -> this.fixOffGraph()));
        this.addJob(new TickJob(200, 300, true, () -> this.dayCheck()));
        this.addJob(new TickJob(200, 100, true, () -> this.crowdingCheck()));
        this.addJob(new TickJob(40, 100, true, new BedCheckRunnable(this)));
        this.addJob(new TickJob(20, 30, false, () -> {
            VillageManager vm = VillageManager.get(this.field_70170_p);
            vm.addScanBox(new AxisAlignedBB(this.func_180425_c()).func_186662_g(64.0));
        }));
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110140_aT().func_111150_b(MAX_HUNGER);
        this.func_110140_aT().func_111150_b(MAX_HAPPY);
        this.func_110140_aT().func_111150_b(MAX_INTELLIGENCE);
        this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(64.0);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.4);
        this.func_110148_a(MAX_HUNGER).func_111128_a(100.0);
        this.func_110148_a(MAX_HAPPY).func_111128_a(100.0);
        this.func_110148_a(MAX_INTELLIGENCE).func_111128_a(100.0);
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(20.0);
        this.field_70180_af.func_187227_b(HUNGER, this.getMaxHunger());
        this.field_70180_af.func_187227_b(HAPPY, this.getMaxHappy());
        this.field_70180_af.func_187227_b(INTELLIGENCE, this.getMaxIntelligence());
        this.field_70180_af.func_187227_b(FORCE_AXIS, -1);
        this.field_70180_af.func_187227_b(SLEEPING, false);
        this.field_70180_af.func_187227_b(SITTING, false);
        this.field_70180_af.func_187227_b(ACTION_ITEM, ItemStack.field_190927_a);
        this.field_70180_af.func_187227_b(MOVEMENT_MODE, MovementMode.WALK.id);
        this.field_70180_af.func_187227_b(BLESSED, 0);
        for (DataParameter<Integer> dataParameter : SKILLS.values()) {
            this.field_70180_af.func_187227_b(dataParameter, 0);
        }
        for (DataParameter dataParameter : this.aiFilters.values()) {
            this.field_70180_af.func_187227_b(dataParameter, true);
        }
    }

    @Override
    protected void func_70088_a() {
        this.aiFilters = new HashMap<String, DataParameter<Boolean>>();
        this.registerAIFilter("read_book", READ_BOOK);
        this.registerAIFilter("visit_tavern", VISIT_TAVERN);
        this.field_70180_af.func_187214_a(HUNGER, 0);
        this.field_70180_af.func_187214_a(HAPPY, 0);
        this.field_70180_af.func_187214_a(INTELLIGENCE, 1);
        this.field_70180_af.func_187214_a(FORCE_AXIS, -1);
        this.field_70180_af.func_187214_a(SLEEPING, false);
        this.field_70180_af.func_187214_a(SITTING, false);
        this.field_70180_af.func_187214_a(ACTION_ITEM, ItemStack.field_190927_a);
        this.field_70180_af.func_187214_a(MOVEMENT_MODE, (byte)0);
        this.field_70180_af.func_187214_a(BLESSED, 0);
        for (DataParameter<Integer> skill : SKILLS.values()) {
            this.field_70180_af.func_187214_a(skill, 0);
        }
        super.func_70088_a();
        this.onStopSit();
    }

    public boolean isAITick(String aiFilter) {
        return super.isAITick() && this.isAIFilterEnabled(aiFilter);
    }

    protected boolean villageHasStorageCount(Predicate<ItemStack> pred, int count) {
        if (this.hasVillage()) {
            return this.getVillage().getStorageCount(pred) >= count;
        }
        return false;
    }

    protected void crowdingCheck() {
        VillageStructure struct;
        if (!this.isSleeping() && (struct = this.getCurrentStructure()) != this.lastCrowdCheck) {
            float crowdFactor;
            this.lastCrowdCheck = struct;
            if (struct != null && (crowdFactor = struct.getCrowdedFactor()) > 0.0f) {
                int CROWD_PENALTY = -5;
                this.setThought(VillagerThought.CROWDED);
                int penalty = (int)(-5.0f * crowdFactor);
                this.modifyHappy(penalty);
                this.debugOut("Crowding penalty [" + penalty + "] in " + struct.type.name() + " at " + this.func_180425_c());
            }
        }
    }

    public int getDaysAlive() {
        return this.daysAlive;
    }

    protected void dayCheck() {
        int curTime = (int)Village.getTimeOfDay(this.field_70170_p);
        if (curTime < this.dayCheckTime) {
            this.dayCheckTime = curTime;
            ++this.daysAlive;
            this.onNewDay();
        } else {
            this.dayCheckTime = curTime;
        }
    }

    protected void onNewDay() {
        this.randomizeGoals();
    }

    protected void fixOffGraph() {
        Village v;
        if (!this.hasVillage() && (v = VillageManager.get(this.field_70170_p).getNearestVillage(this.func_180425_c(), 130)) != null) {
            BlockPos bestPos = null;
            double bestDist = Double.MAX_VALUE;
            for (BlockPos testPos : BlockPos.func_177980_a((BlockPos)this.func_180425_c().func_177982_a(-3, -3, -3), (BlockPos)this.func_180425_c().func_177982_a(3, 3, 3))) {
                if (!v.getPathingGraph().isInGraph(testPos)) continue;
                double dist = this.func_180425_c().func_177951_i((Vec3i)testPos);
                if (bestPos != null && !(dist < bestDist)) continue;
                bestPos = testPos;
                bestDist = dist;
            }
            if (bestPos != null) {
                this.func_70634_a(bestPos.func_177958_n(), bestPos.func_177956_o(), bestPos.func_177952_p());
            }
        }
    }

    protected void scanForEnemies() {
        if (!this.isRole(VillagerRole.VENDOR) && !this.isRole(VillagerRole.VISITOR)) {
            ListIterator itr = this.field_70170_p.func_175647_a(EntityLiving.class, this.func_174813_aQ().func_72314_b(30.0, 6.0, 30.0), this.isEnemy()).listIterator();
            while (itr.hasNext()) {
                double enemyRange;
                double distMe;
                IAttributeInstance attribute;
                EntityLiving enemy = (EntityLiving)itr.next();
                if (this.func_70685_l((Entity)enemy) && this.hasVillage()) {
                    this.getVillage().addOrRenewEnemy((EntityLivingBase)enemy, 1);
                }
                if ((attribute = enemy.func_110148_a(SharedMonsterAttributes.field_111265_b)) == null || !((distMe = enemy.func_70068_e((Entity)this)) < (enemyRange = attribute.func_111126_e() * attribute.func_111126_e()))) continue;
                if (enemy.func_70638_az() == null) {
                    enemy.func_70624_b((EntityLivingBase)this);
                    continue;
                }
                double distTarget = enemy.func_70068_e((Entity)enemy.func_70638_az());
                if (distMe < distTarget) {
                    enemy.func_70624_b((EntityLivingBase)this);
                    continue;
                }
                if (!this.hasVillage()) continue;
                boolean meIndoors = this.getVillage().isInStructure(this.func_180425_c());
                boolean currentTargetIndoors = this.getVillage().isInStructure(enemy.func_70638_az().func_180425_c());
                if (meIndoors || !currentTargetIndoors) continue;
                enemy.func_70624_b((EntityLivingBase)this);
            }
        }
    }

    public void func_70675_k(float damage) {
        if (damage < 1.0f) {
            damage = 1.0f;
        }
        int finalDmg = (int)damage;
        this.func_184193_aE().forEach(a -> {
            if (a.func_77973_b() instanceof ItemArmor && this.func_70681_au().nextBoolean()) {
                this.damageItem((ItemStack)a, finalDmg);
            }
        });
    }

    public void damageItem(ItemStack itemStack, int amount) {
        int itemDamage = ModItems.isTaggedItem(itemStack, ItemTagType.VILLAGER) ? amount : amount * 5;
        ItemStack oldItem = null;
        if (itemStack.func_77952_i() + itemDamage >= itemStack.func_77958_k()) {
            oldItem = itemStack.func_77946_l();
        }
        itemStack.func_77972_a(itemDamage, (EntityLivingBase)this);
        if (itemStack.func_190926_b() && oldItem != null) {
            this.onInventoryUpdated(oldItem);
        }
    }

    @Nullable
    public Entity changeDimension(int dimensionIn, ITeleporter teleporter) {
        return null;
    }

    public boolean func_70104_M() {
        if (this.isSitting() || this.isSleeping()) {
            return false;
        }
        return super.func_70104_M();
    }

    protected void func_82167_n(Entity entityIn) {
        if (!this.isSitting()) {
            if (entityIn instanceof EntityVillagerTek && this.isDoorNearby(1, 1)) {
                return;
            }
            super.func_82167_n(entityIn);
        }
    }

    protected void func_85033_bc() {
        if (!this.isSitting() && !this.isSleeping()) {
            super.func_85033_bc();
        }
    }

    protected boolean isDoorNearby(int xx, int zz) {
        for (int x = -xx; x <= xx; ++x) {
            for (int z = -zz; z <= zz; ++z) {
                BlockPos bp = this.func_180425_c().func_177965_g(x).func_177964_d(z);
                if (!VillageStructure.isWoodDoor(this.field_70170_p, bp) && !VillageStructure.isGate(this.field_70170_p, bp)) continue;
                return true;
            }
        }
        return false;
    }

    protected SoundEvent func_184639_G() {
        if (this.isSleeping() && this.field_70170_p.field_73012_v.nextInt(2) == 0) {
            return ModSoundEvents.villagerSleep;
        }
        return null;
    }

    protected void bedCheck() {
        if (this.hasVillage()) {
            VillageStructureHome home;
            VillageStructure struct;
            if (this.getBedPos() != null && this.homeFrame != null && (struct = this.village.getStructureFromFrame(this.homeFrame)) != null && struct instanceof VillageStructureHome && !(home = (VillageStructureHome)struct).canVillagerSleep(this)) {
                this.clearHome();
            }
            if (this.getBedPos() == null) {
                if (this.homeFrame != null) {
                    struct = this.village.getStructureFromFrame(this.homeFrame);
                    if (struct != null && struct instanceof VillageStructureHome) {
                        home = (VillageStructureHome)struct;
                        if (home.canVillagerSleep(this) && !home.isFull()) {
                            this.homeFrame = null;
                            home.addResident(this);
                        } else {
                            this.clearHome();
                        }
                    } else if (this.field_70173_aa > 200) {
                        this.clearHome();
                    }
                } else {
                    VillageStructureHome home2 = this.village.getAvailableHome(this);
                    if (home2 != null) {
                        home2.addResident(this);
                    }
                }
            }
        }
    }

    public VillageStructure getCurrentStructure() {
        if (this.hasVillage()) {
            return this.getVillage().getStructure(this.func_180425_c());
        }
        return null;
    }

    public static Function<EntityVillagerTek, VillageStructure> findLocalTavern() {
        return p -> {
            VillageStructure tavern;
            if (p.hasVillage() && (tavern = p.getVillage().getNearestStructure(VillageStructureType.TAVERN, p.func_180425_c())) != null) {
                return tavern;
            }
            return null;
        };
    }

    public static Function<EntityVillagerTek, VillageStructure> getVillagerHome() {
        return p -> {
            if (p.func_110175_bO()) {
                return p.getVillage().getStructureFromFrame(p.homeFrame);
            }
            return null;
        };
    }

    @Override
    public void attachToVillage(Village v) {
        super.attachToVillage(v);
        this.sleepOffset = this.genOffset(400);
        if (this.isRole(VillagerRole.VILLAGER)) {
            v.addResident(this);
        }
        if (this.getIntelligence() < 1) {
            this.setIntelligence(1);
        }
    }

    @Override
    protected void detachVillage() {
        if (this.hasVillage()) {
            this.village.removeResident(this);
        }
        super.detachVillage();
    }

    protected int genOffset(int range) {
        return this.func_70681_au().nextInt(range) - range / 2;
    }

    protected void randomizeGoals() {
        this.wantsLearning = this.func_70681_au().nextInt(100) < 28 && this.getIntelligence() < this.getMaxIntelligence() ? this.func_70681_au().nextInt(6) + 3 : 0;
        this.wantsTavern = this.func_70681_au().nextInt(10) < 3;
    }

    private static void cleanUpInventory(EntityVillagerTek v) {
        v.cleanUpInventory();
    }

    protected void cleanUpInventory() {
    }

    protected void func_70670_a(PotionEffect effect) {
        if (effect.func_188419_a() == ModPotions.potionBless) {
            this.field_70180_af.func_187227_b(BLESSED, effect.func_76458_c());
        }
        super.func_70670_a(effect);
    }

    protected void func_70688_c(PotionEffect effect) {
        if (effect.func_188419_a() == ModPotions.potionBless) {
            this.field_70180_af.func_187227_b(BLESSED, 0);
        }
        super.func_70688_c(effect);
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
        if (this.func_110167_bD() && this.func_70681_au().nextInt(this.getHappy() < 50 ? 30 : 50) == 0) {
            this.modifyHappy(-1);
            if (this.getHappy() <= 0) {
                this.func_110160_i(true, true);
            }
        }
        if (!this.isWorldRemote()) {
            ++this.lastSadTick;
            if (this.nextThought != null && this.field_70173_aa % 80 == 0) {
                TekVillager.NETWORK.sendToAllAround((IMessage)this.nextThought, new NetworkRegistry.TargetPoint(this.getDimension(), this.field_70165_t, this.field_70163_u, this.field_70161_v, 64.0));
                this.nextThought = null;
            }
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    protected void startWalking() {
        MovementMode mode = this.getMovementMode();
        if (mode != this.lastMovementMode) {
            if (this.lastMovementMode != null) {
                this.stopWalking();
            }
            this.lastMovementMode = mode;
            if (mode != null) {
                this.playClientAnimation(mode.anim);
            }
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    protected void stopWalking() {
        if (this.lastMovementMode != null) {
            this.stopClientAnimation(this.lastMovementMode.anim);
            this.lastMovementMode = null;
        }
    }

    public BlockPos getBedPos() {
        return this.bedPos;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void func_180426_a(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        super.func_180426_a(x, y, z, yaw, pitch, 1, teleport);
    }

    public void setHome(BlockPos bedPos, BlockPos homeFrame) {
        this.bedPos = bedPos;
        this.homeFrame = homeFrame;
    }

    public void clearHome() {
        this.bedPos = null;
        this.homeFrame = null;
    }

    public boolean func_110175_bO() {
        return this.hasVillage() && this.homeFrame != null;
    }

    public VillageStructureHome getHome() {
        if (this.func_110175_bO()) {
            VillageStructureHome home = (VillageStructureHome)this.village.getStructureFromFrame(this.homeFrame);
            return home;
        }
        return null;
    }

    public void addVillagerPosition() {
        if (this.hasVillage()) {
            this.getVillage().addVillagerPosition(this);
        }
    }

    public final int getMaxHunger() {
        return (int)this.func_110148_a(MAX_HUNGER).func_111126_e();
    }

    public int getHunger() {
        return (Integer)this.field_70180_af.func_187225_a(HUNGER);
    }

    public void setHunger(int hunger) {
        if (this.isRole(VillagerRole.VILLAGER)) {
            this.field_70180_af.func_187227_b(HUNGER, MathHelper.func_76125_a((int)hunger, (int)0, (int)this.getMaxHunger()));
            if (hunger < 0 && this.isHungry()) {
                this.setThought(VillagerThought.HUNGRY);
            }
        }
    }

    public final int getMaxHappy() {
        return (int)this.func_110148_a(MAX_HAPPY).func_111126_e();
    }

    public int getHappy() {
        return (Integer)this.field_70180_af.func_187225_a(HAPPY);
    }

    public int setHappy(int happy) {
        int prevHappy = this.getHappy();
        this.field_70180_af.func_187227_b(HAPPY, MathHelper.func_76125_a((int)happy, (int)0, (int)this.getMaxHappy()));
        return this.getHappy() - prevHappy;
    }

    public final int getMaxIntelligence() {
        return (int)this.func_110148_a(MAX_INTELLIGENCE).func_111126_e();
    }

    public int getIntelligence() {
        int intel = Math.max((Integer)this.field_70180_af.func_187225_a(INTELLIGENCE), 1);
        return intel;
    }

    public void setIntelligence(int intel) {
        this.field_70180_af.func_187227_b(INTELLIGENCE, MathHelper.func_76125_a((int)intel, (int)1, (int)this.getMaxIntelligence()));
    }

    public int getBlessed() {
        return (Integer)this.field_70180_af.func_187225_a(BLESSED);
    }

    public int getSkill(ProfessionType pt) {
        int skill = this.getBaseSkill(pt);
        if (pt == this.getProfessionType()) {
            skill += this.getBlessed();
        }
        return Math.min(skill, 100);
    }

    public int getBaseSkill(ProfessionType pt) {
        int skill = (Integer)this.field_70180_af.func_187225_a(SKILLS.get((Object)pt));
        return Math.min(skill, 100);
    }

    public int getSkillLerp(ProfessionType pt, int min, int max) {
        if (min < max) {
            return (int)MathHelper.func_151238_b((double)min, (double)max, (double)((double)this.getSkill(pt) / 100.0));
        }
        return (int)MathHelper.func_151238_b((double)max, (double)min, (double)((100.0 - (double)this.getSkill(pt)) / 100.0));
    }

    public void setSkill(ProfessionType pt, int val) {
        this.debugOut("Skill Change - " + pt.name + " --> " + val);
        this.field_70180_af.func_187227_b(SKILLS.get((Object)pt), MathHelper.func_76125_a((int)val, (int)0, (int)100));
    }

    public int getForceAxis() {
        return (Integer)this.field_70180_af.func_187225_a(FORCE_AXIS);
    }

    public void setForceAxis(int axes) {
        this.field_70180_af.func_187227_b(FORCE_AXIS, axes);
    }

    public boolean isSleeping() {
        return (Boolean)this.field_70180_af.func_187225_a(SLEEPING);
    }

    public boolean isSitting() {
        return (Boolean)this.field_70180_af.func_187225_a(SITTING);
    }

    public MovementMode getMovementMode() {
        MovementMode mode = MovementMode.valueOf((Byte)this.field_70180_af.func_187225_a(MOVEMENT_MODE));
        return mode;
    }

    public void setMovementMode(MovementMode mode) {
        this.field_70180_af.func_187227_b(MOVEMENT_MODE, mode.id);
    }

    protected void setSleeping(boolean sleep) {
        this.field_70180_af.func_187227_b(SLEEPING, sleep);
    }

    public void setSitting(boolean sit) {
        this.field_70180_af.func_187227_b(SITTING, sit);
    }

    public boolean isHungry() {
        return this.getHunger() < 30;
    }

    public boolean isStarving() {
        return this.getHunger() <= 0;
    }

    public boolean shouldSleep() {
        return this.isSleepingTime() || !this.isRole(VillagerRole.DEFENDER) && this.func_110143_aJ() < this.func_110138_aP() * 0.5f;
    }

    public boolean isSleepingTime() {
        return Village.isTimeOfDay(this.field_70170_p, SLEEP_START_TIME + this.sleepOffset + (this.wantsTavern() ? 4000 : 0), SLEEP_END_TIME + this.sleepOffset);
    }

    public boolean isWorkTime() {
        return Village.isTimeOfDay(this.field_70170_p, WORK_START_TIME, WORK_END_TIME, this.sleepOffset) && !this.field_70170_p.func_72896_J();
    }

    public boolean isLearningTime() {
        if (this.wantsLearning > 0 && Village.isTimeOfDay(this.field_70170_p, this.sleepOffset + 2500, this.sleepOffset + 8000)) {
            return this.isAIFilterEnabled("read_book");
        }
        return false;
    }

    public void addIntelligence(int delta) {
        if (delta > 0 && this.getIntelligence() < 100 && this.func_70681_au().nextInt(100) * 2 > this.getIntelligence()) {
            this.setIntelligence(this.getIntelligence() + delta);
            this.setItemThought(Items.field_151122_aG);
            --this.wantsLearning;
        }
    }

    public void addIntelligenceDelay(int delta, int ticks) {
        this.addJob(new TickJob(ticks, 0, false, () -> this.addIntelligence(delta)));
    }

    public void incrementSkill(ProfessionType pt) {
        List<EntityChild> children;
        this.setSkill(pt, this.getBaseSkill(pt) + 1);
        this.setItemThought(ModItems.getProfessionToken(pt));
        this.skillUpdated(pt);
        if (!this.func_70631_g_() && !(children = this.field_70170_p.func_72872_a(EntityChild.class, this.func_174813_aQ().func_72314_b(12.0, 8.0, 12.0))).isEmpty()) {
            children.forEach(c -> {
                VillageStructure struct;
                boolean proximityLearn = true;
                if (c.hasVillage() && (struct = c.getVillage().getStructure(c.func_180425_c())) != null && struct instanceof VillageStructureSchool) {
                    proximityLearn = false;
                }
                if (proximityLearn && c.getSkill(pt) < this.getSkill(pt) / 2) {
                    int chance = Math.max(c.getBaseSkill(pt) / 2, 1);
                    if (c.func_70681_au().nextInt(chance) == 0) {
                        c.incrementSkill(pt);
                    }
                }
            });
        }
    }

    public void setIdle(int idle) {
        this.idle = idle;
    }

    public int getIdle() {
        return this.idle;
    }

    protected void skillUpdated(ProfessionType pt) {
    }

    public void tryAddSkill(ProfessionType pt, int chance) {
        double skill;
        if (!this.field_70170_p.field_72995_K && (skill = (double)this.getBaseSkill(pt)) < 100.0) {
            double intel = Math.max((double)this.getIntelligence(), 5.0);
            double gapMod = 1.0 / Math.pow(skill / intel, 2.0);
            double intMod = 0.2;
            double intCheck = Math.min(gapMod * 0.2, 1.0);
            int rate = FMLCommonHandler.instance().getMinecraftServerInstance().func_71218_a(0).func_82736_K().func_180263_c("villagerSkillRate");
            double roll = this.field_70170_p.field_73012_v.nextDouble();
            double rollModded = roll * (double)rate / 100.0;
            if (rollModded <= intCheck && this.field_70170_p.field_73012_v.nextInt(chance) == 0) {
                this.incrementSkill(pt);
            }
        }
    }

    public void modifyHungerDelay(int delta, int ticks) {
        this.addJob(new TickJob(ticks, 0, false, () -> this.modifyHunger(delta)));
    }

    public void modifyHunger(int delta) {
        if (delta < 0 && this.hasVillage() && !this.getVillage().hasStructure(VillageStructureType.STORAGE)) {
            return;
        }
        this.setHunger(this.getHunger() + delta);
    }

    public void modifyHappyDelay(int delta, int ticks) {
        this.addJob(new TickJob(ticks, 0, false, () -> this.modifyHappy(delta)));
    }

    public void modifyHappy(int delta) {
        if (delta > 0) {
            this.field_70170_p.func_72960_a((Entity)this, (byte)14);
            if (this.func_70681_au().nextInt(6) == 0 && !this.isSleeping()) {
                this.playSound(ModSoundEvents.villagerHappy);
            }
        } else if (delta < 0) {
            this.field_70170_p.func_72960_a((Entity)this, (byte)13);
            if (this.func_70681_au().nextInt(2) == 0 && !this.isSleeping()) {
                this.playSound(ModSoundEvents.villagerAngry);
            }
        }
        int realChange = this.setHappy(this.getHappy() + delta);
        if (this.hasVillage() && realChange != 0) {
            this.getVillage().trackHappy(realChange);
        }
    }

    public void throttledSadness(int delta) {
        if (this.lastSadTick > this.lastSadThrottle && this.func_70681_au().nextBoolean()) {
            this.addJob(new TickJob(20, 60, false, () -> this.modifyHappy(delta)));
            this.lastSadTick = 0;
            this.lastSadThrottle = 50 + this.func_70681_au().nextInt(100);
        }
    }

    public void cheerBeer(int happy) {
        int offset = this.func_70681_au().nextInt(25);
        if (!this.isPlayingAnimation("villager_sit_cheer")) {
            if (this.isSitting()) {
                this.addJob(new TickJob(offset, 0, false, () -> this.playServerAnimation("villager_sit_cheer")));
                this.addJob(new TickJob(8 + offset, 0, false, () -> this.equipActionItem(new ItemStack((Item)ModItems.beer))));
                this.addJob(new TickJob(52 + offset, 0, false, () -> this.unequipActionItem()));
                this.addJob(new TickJob(58 + offset, 0, false, () -> {
                    if (this.isSitting()) {
                        this.playServerAnimation("villager_sit");
                    }
                }));
            }
            this.addJob(new TickJob(10 + offset * 2, 0, false, () -> this.func_184185_a(ModSoundEvents.villagerHappy, 1.2f, this.func_70681_au().nextFloat() * 0.4f + 0.8f)));
            this.addJob(new TickJob(40, 0, false, () -> this.modifyHappy(happy)));
        }
    }

    public void func_184178_b(EntityPlayerMP player) {
        super.func_184178_b(player);
        if (!this.curAnim.isEmpty()) {
            this.playServerAnimation(this.curAnim);
        }
    }

    @Override
    public float func_70689_ay() {
        float baseSpeed;
        float modifiedSpeed = baseSpeed = 0.45f * this.getMovementMode().speedMult;
        int blessed = this.getBlessed();
        if (blessed > 0) {
            modifiedSpeed = (float)((double)modifiedSpeed * (1.05 + (double)blessed * 0.002));
        }
        return modifiedSpeed;
    }

    public void setLeadAnimal(EntityAnimal animal) {
        this.leadAnimal = animal;
    }

    public EntityAnimal getLeadAnimal() {
        return this.leadAnimal;
    }

    protected boolean func_70692_ba() {
        return false;
    }

    public VillagerInventory getInventory() {
        return this.villagerInventory;
    }

    public ItemDesireSet getDesireSet() {
        return this.desireSet;
    }

    public void onStorageChange(ItemStack storageItem) {
        this.desireSet.onStorageUpdated(this, storageItem);
    }

    public void onInventoryUpdated(ItemStack updatedItem) {
        this.desireSet.onInventoryUpdated(this, updatedItem);
    }

    protected boolean canVillagerPickupItem(ItemStack itemIn) {
        return false;
    }

    public boolean func_189652_ae() {
        return this.isSitting() || super.func_189652_ae();
    }

    public double getSitOffset() {
        return 0.0;
    }

    public void onStartSit(int sitAxis) {
        this.setForceAxis(sitAxis);
        this.setSitting(true);
        this.equipActionItem(ModItems.EMPTY_HAND_ITEM);
        if (!this.curAnim.isEmpty() && this.curAnim != "villager_sit") {
            this.stopServerAnimation(this.curAnim);
        }
        this.playServerAnimation("villager_sit");
    }

    public void onStopSit() {
        this.setSitting(false);
        this.setForceAxis(-1);
        this.stopServerAnimation("villager_sit");
        this.func_189654_d(false);
        this.unequipActionItem(ModItems.EMPTY_HAND_ITEM);
    }

    public void onStartSleep(int sleepAxis) {
        this.setForceAxis(sleepAxis);
        this.setSleeping(true);
        if (!this.curAnim.isEmpty() && this.curAnim != "villager_sleep") {
            this.stopServerAnimation(this.curAnim);
        }
        this.playServerAnimation("villager_sleep");
    }

    public void onStopSleep() {
        if (this.isSleeping() && !this.isSleepingTime()) {
            this.checkSpawnHeart();
            this.modifyHappy(this.field_70146_Z.nextInt(20) + 10);
        }
        this.setForceAxis(-1);
        this.setSleeping(false);
        this.stopServerAnimation("villager_sleep");
    }

    private void checkSpawnHeart() {
        int MIN_HAPPY = (int)((float)this.getMaxHappy() * 0.7f);
        if (this.hasVillage() && this.getHappy() >= MIN_HAPPY) {
            VillageStructure struct;
            IVillageData vd;
            float happyFactor = (float)(this.getMaxHappy() - this.getHappy()) / (float)(this.getMaxHappy() - MIN_HAPPY);
            int CONSTANT_DIFFICULTY = 15;
            int chance = 15 + this.village.getResidentCount() + (int)((float)this.village.getResidentCount() * happyFactor * 2.0f);
            if (this.func_70681_au().nextInt(chance) == 0 && this.hasVillage() && !this.func_70631_g_() && this.getProfessionType() != ProfessionType.NITWIT && (vd = this.getVillage().getTownData()) != null && this.bedPos != null && vd.isChildReady(this.field_70170_p.func_82737_E()) && (struct = EntityVillagerTek.getVillagerHome().apply(this)) != null && struct.type.isHome()) {
                VillageStructureHome home = (VillageStructureHome)struct;
                vd.childSpawned(this.field_70170_p);
                this.addJob(new TickJob(100, 0, false, () -> {
                    ItemStack itemHeart = ModItems.createTaggedItem(ModItems.heart, ItemTagType.VILLAGER);
                    itemHeart.func_179543_a("village").func_186854_a("parent", this.func_110124_au());
                    EntityItem heartEntity = new EntityItem(this.field_70170_p, (double)this.bedPos.func_177958_n(), (double)(this.bedPos.func_177956_o() + 1), (double)this.bedPos.func_177952_p(), itemHeart);
                    this.field_70170_p.func_72838_d((Entity)heartEntity);
                }));
            }
        }
    }

    public MovementMode getDefaultMovement() {
        if (this.getHappy() < this.getMaxHappy() / 5) {
            return MovementMode.SULK;
        }
        return MovementMode.WALK;
    }

    @Override
    public void updateMovement(boolean arrived) {
        if (this.field_70170_p.field_73012_v.nextInt(50) == 0) {
            this.modifyHunger(-1);
        }
        if (!arrived && this.hasVillage() && this.func_70681_au().nextInt(20) == 0) {
            this.addVillagerPosition();
        }
    }

    @Override
    public void resetMovement() {
        super.resetMovement();
    }

    protected ItemStack modifyPickUpStack(ItemStack itemStack) {
        return itemStack;
    }

    public void func_175445_a(EntityItem itemEntity) {
        ItemStack modStack;
        ItemStack itemStack = itemEntity.func_92059_d();
        if (this.canVillagerPickupItem(itemStack) && ModItems.canVillagerSee(itemStack) && !(modStack = this.modifyPickUpStack(itemStack.func_77946_l())).func_190926_b()) {
            ItemStack leftOverStack = this.villagerInventory.func_174894_a(modStack);
            if (leftOverStack.func_190926_b()) {
                itemEntity.func_70106_y();
            } else {
                int actuallyTaken = modStack.func_190916_E() - leftOverStack.func_190916_E();
                int newCount = itemStack.func_190916_E() - actuallyTaken;
                if (newCount <= 0) {
                    itemEntity.func_70106_y();
                } else {
                    itemStack.func_190920_e(newCount);
                }
            }
        }
    }

    public boolean func_180431_b(DamageSource source) {
        if (source == DamageSource.field_76368_d) {
            return true;
        }
        return super.func_180431_b(source);
    }

    public boolean func_70097_a(DamageSource source, float amount) {
        float beforeHealth = this.func_110143_aJ();
        if (super.func_70097_a(source, amount)) {
            float afterHealth = this.func_110143_aJ();
            float actualDamage = beforeHealth - afterHealth;
            if (actualDamage > 0.0f) {
                if (!this.isRole(VillagerRole.DEFENDER)) {
                    this.modifyHappy(-8);
                }
                if (this.hasVillage() && actualDamage > 0.0f) {
                    this.getVillage().reportVillagerDamage(this, source, actualDamage);
                }
                if (this.isSleeping()) {
                    this.onStopSleep();
                }
            }
            return true;
        }
        return false;
    }

    public void func_70645_a(DamageSource cause) {
        if (this.hasVillage() && this.getBedPos() != null) {
            int happyMod = -25;
            if (this.func_70631_g_()) {
                happyMod *= 2;
            }
            List<EntityVillagerTek> villagers = this.field_70170_p.func_175647_a(EntityVillagerTek.class, this.func_174813_aQ().func_186662_g(200.0), p -> p != this);
            for (EntityVillagerTek v : villagers) {
                if (v.getVillage() != this.getVillage() || v.getBedPos() == null || v.isSleeping()) continue;
                v.modifyHappy(happyMod - this.func_70681_au().nextInt(15));
            }
        }
        if (this.hasVillage() && this.isRole(VillagerRole.VILLAGER)) {
            this.getVillage().reportVillagerDeath(this, cause);
        }
        super.func_70645_a(cause);
        if (!this.field_70170_p.field_72995_K) {
            this.dropAllItems();
        }
    }

    private void dropAllItems() {
        VillagerInventory inv = this.getInventory();
        for (int i = 0; i < inv.func_70302_i_(); ++i) {
            ItemStack itemStack = inv.func_70301_a(i);
            if (itemStack.func_190926_b()) continue;
            this.func_70099_a(itemStack, 0.5f);
        }
        inv.func_174888_l();
    }

    public void pickupItems(int grow) {
        for (EntityItem entityitem : this.field_70170_p.func_72872_a(EntityItem.class, this.func_174813_aQ().func_72314_b((double)grow, 3.0, (double)grow))) {
            if (entityitem.field_70128_L || entityitem.func_92059_d().func_190926_b() || entityitem.func_174874_s()) continue;
            this.func_175445_a(entityitem);
        }
    }

    public void setThought(VillagerThought thought) {
        this.nextThought = new PacketVillagerThought(this, thought, thought.getScale());
    }

    public void setItemThought(Item item) {
        ModEntities.sendItemThought((Entity)this, item);
    }

    @SideOnly(value=Side.CLIENT)
    public void func_70103_a(byte id) {
        if (id == 12) {
            this.spawnParticles(EnumParticleTypes.HEART);
        } else if (id == 13) {
            this.spawnParticles(EnumParticleTypes.VILLAGER_ANGRY);
        } else if (id == 14) {
            this.spawnParticles(EnumParticleTypes.VILLAGER_HAPPY);
        } else {
            super.func_70103_a(id);
        }
    }

    @SideOnly(value=Side.CLIENT)
    private void spawnParticles(EnumParticleTypes particleType) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.field_70146_Z.nextGaussian() * 0.02;
            double d1 = this.field_70146_Z.nextGaussian() * 0.02;
            double d2 = this.field_70146_Z.nextGaussian() * 0.02;
            this.field_70170_p.func_175688_a(particleType, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0f) - (double)this.field_70130_N, this.field_70163_u + 1.0 + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0f) - (double)this.field_70130_N, d0, d1, d2, new int[0]);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void handleThought(PacketVillagerThought msg) {
        ParticleThought part = new ParticleThought(this.field_70170_p, Minecraft.func_71410_x().func_110434_K(), (Entity)this, msg.getScale(), msg.getThought().getTex());
        Minecraft.func_71410_x().field_71452_i.func_78873_a((Particle)part);
    }

    public boolean func_184645_a(EntityPlayer player, EnumHand hand) {
        ItemProfessionToken token;
        ItemStack itemStack = player.func_184586_b(hand);
        if (itemStack.func_77973_b() == Items.field_151057_cb) {
            itemStack.func_111282_a(player, (EntityLivingBase)this, hand);
            return true;
        }
        if (itemStack.func_77973_b() instanceof ItemProfessionToken && this.hasVillage() && this.canConvertProfession((token = (ItemProfessionToken)itemStack.func_77973_b()).getProfessionType()) && (ModItems.isItemVillageBound(itemStack, this.getVillage()) || !ModItems.isItemVillageBound(itemStack)) && !this.field_70170_p.field_72995_K) {
            itemStack.func_190918_g(1);
            EntityVillagerTek villager = token.createVillager(this.field_70170_p, this);
            if (villager != null) {
                villager.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
                villager.func_180482_a(this.field_70170_p.func_175649_E(this.func_180425_c()), null);
                villager.cloneFrom(this);
                this.field_70170_p.func_72838_d((Entity)villager);
                player.func_184185_a(SoundEvents.field_193784_dd, 1.0f, 1.0f);
                return true;
            }
            return false;
        }
        if (!this.field_70170_p.field_72995_K) {
            try {
                java.lang.reflect.Method openGui = player.getClass().getMethod("openGui", Object.class, Integer.TYPE, World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                openGui.invoke((Object)player, TekVillager.instance, 0, this.field_70170_p, this.func_145782_y(), 0, 0);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                this.debugOut("Unable to open villager GUI: " + reflectiveOperationException.getClass().getSimpleName());
            }
            this.func_70661_as().func_75499_g();
        }
        if (player.func_70093_af()) {
            this.debugSpam();
        }
        return true;
    }

    protected void debugSpam() {
        this.debugOut("+ + + + + + + + + + + + + +");
        this.debugOut("Debug for " + this.getDebugName());
        this.getInventory().debugSpam();
        this.debugOut("    Active Task dump unavailable in decompiled workspace.");
        this.debugOut("Hunger: " + this.getHunger());
        this.debugOut("Happy: " + this.getHappy());
        this.debugOut("Health: " + this.func_110143_aJ());
        this.debugOut("Intelligence: " + this.getIntelligence());
        for (ProfessionType pt : ProfessionType.values()) {
            if (this.getBaseSkill(pt) <= 0) continue;
            this.debugOut("     " + pt.name + ": " + this.getBaseSkill(pt));
        }
        this.debugOut("^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^");
    }

    protected void registerAIFilter(String filterName, DataParameter<Boolean> param) {
        if (this.aiFilters.put(filterName, param) != null) {
            this.debugOut("ERROR: registerAIFilter( " + filterName + " ).  Double registration");
        }
        this.field_70180_af.func_187214_a(param, true);
    }

    protected void removeAIFilter(String filterName) {
        this.aiFilters.remove(filterName);
    }

    public List<String> getAIFilters() {
        return new ArrayList<String>(this.aiFilters.keySet());
    }

    public boolean isAIFilterEnabled(String filterName) {
        DataParameter<Boolean> param = this.aiFilters.get(filterName);
        if (param != null) {
            boolean result = (Boolean)this.field_70180_af.func_187225_a(param);
            return result;
        }
        this.debugOut("ERROR: (isAIFilterEnabled) AI Filter " + filterName + " does not exist!");
        this.debugOut("ERROR: (isAIFilterEnabled) AI Filter " + filterName + " does not exist!");
        this.debugOut("ERROR: (isAIFilterEnabled) AI Filter " + filterName + " does not exist!");
        this.debugOut("ERROR: (isAIFilterEnabled) AI Filter " + filterName + " does not exist!");
        this.debugOut("ERROR: (isAIFilterEnabled) AI Filter " + filterName + " does not exist!");
        return true;
    }

    public void setAIFilter(String filterName, boolean enabled) {
        DataParameter<Boolean> param = this.aiFilters.get(filterName);
        if (param != null) {
            this.debugOut("AI Filer " + filterName + " -> " + enabled);
            this.field_70180_af.func_187227_b(param, enabled);
        } else {
            this.debugOut("ERROR: (setAIFilter) AI Filter " + filterName + " does not exist!");
        }
    }

    public boolean canConvertProfession(ProfessionType pt) {
        return pt != this.professionType && pt != ProfessionType.CAPTAIN;
    }

    public void func_70604_c(@Nullable EntityLivingBase target) {
        if (target instanceof EntityPlayer) {
            return;
        }
        super.func_70604_c(target);
        if (this.hasVillage() && target != null) {
            this.getVillage().addOrRenewEnemy(target, 5);
            if (this.func_70089_S()) {
                this.field_70170_p.func_72960_a((Entity)this, (byte)14);
            }
        }
    }

    @Override
    public void func_184206_a(DataParameter<?> key) {
        super.func_184206_a(key);
        if (MOVEMENT_MODE.equals(key) && this.isWalking()) {
            this.startWalking();
        }
    }

    public void equipActionItem(ItemStack toolItem) {
        this.field_70180_af.func_187227_b(ACTION_ITEM, toolItem.func_77946_l());
    }

    public ItemStack getActionItem() {
        return (ItemStack)this.field_70180_af.func_187225_a(ACTION_ITEM);
    }

    public void unequipActionItem() {
        this.field_70180_af.func_187227_b(ACTION_ITEM, ItemStack.field_190927_a);
    }

    public void unequipActionItem(ItemStack actionItem) {
        if (actionItem != null && actionItem.func_77973_b() == this.getActionItem().func_77973_b()) {
            this.field_70180_af.func_187227_b(ACTION_ITEM, ItemStack.field_190927_a);
        }
    }

    public com.google.common.base.Predicate<Entity> isEnemy() {
        return e -> this.isHostile().test(e) || e instanceof EntityLivingBase && EntityNecromancer.isMinion((EntityLivingBase)e);
    }

    public com.google.common.base.Predicate<Entity> isHostile() {
        return e -> e instanceof EntityZombie && !(e instanceof EntityPigZombie) || e instanceof EntityWitherSkeleton || e instanceof EntityEvoker || e instanceof EntityVex || e instanceof EntityVindicator || e instanceof EntityNecromancer;
    }

    public boolean isFleeFrom(Entity e) {
        return this.isHostile().test(e);
    }

    public void addRecentEat(Item item) {
        this.recentEats.add(Item.func_150891_b((Item)item));
        while (this.recentEats.size() > 5) {
            this.recentEats.remove();
        }
    }

    public int getRecentEatModifier(Item item) {
        int itemId = Item.func_150891_b((Item)item);
        int eatCount = MathHelper.func_76125_a((int)((int)this.recentEats.stream().filter(i -> i == itemId).count()), (int)0, (int)5);
        return recentEatPenalties[eatCount];
    }

    public static Function<ItemStack, Integer> foodBetterThan(EntityVillagerTek v, int foodValue) {
        return p -> {
            int val = EntityVillagerTek.foodValue(v).apply((ItemStack)p);
            return val > foodValue ? val : -1;
        };
    }

    public static Function<ItemStack, Integer> foodValue(EntityVillagerTek v) {
        return p -> (int)((ModItems.isTaggedItem(p, ItemTagType.VILLAGER) ? 1.0f : 0.5f) * (float)EntityVillagerTek.foodItemValue(v).apply(p.func_77973_b()).intValue());
    }

    public static Function<Item, Integer> foodItemValue(EntityVillagerTek v) {
        return i -> EntityAIEatFood.getFoodScore(i, v);
    }

    public boolean isVillageMember(Village v) {
        return this.getVillage() == v && this.getBedPos() != null;
    }

    public boolean isMale() {
        return this.func_110124_au().getLeastSignificantBits() % 2L == 0L;
    }

    public Predicate<ItemStack> isHarvestItem() {
        return p -> false;
    }

    public void equipBestGear() {
    }

    public void equipBestGear(EntityEquipmentSlot slot, Function<ItemStack, Integer> bestFunc) {
        ItemStack oldGear;
        ItemStack bestItem = this.func_184582_a(slot);
        int bestScore = bestFunc.apply(bestItem);
        int swapSlot = -1;
        for (int i = 0; i < this.getInventory().func_70302_i_(); ++i) {
            ItemStack itemStack = this.getInventory().func_70301_a(i);
            int thisScore = bestFunc.apply(itemStack);
            if (thisScore <= bestScore) continue;
            bestScore = thisScore;
            bestItem = itemStack;
            swapSlot = i;
        }
        if (swapSlot >= 0) {
            ItemStack oldGear2 = this.func_184582_a(slot);
            this.func_184201_a(slot, bestItem);
            this.getInventory().func_70299_a(swapSlot, oldGear2);
            this.debugOut("Equipping new gear: " + bestItem + "   Removing old gear: " + oldGear2);
        } else if (bestScore == -1 && !(oldGear = this.func_184582_a(slot)).func_190926_b()) {
            this.func_184201_a(slot, ItemStack.field_190927_a);
            this.getInventory().func_174894_a(oldGear);
        }
    }

    protected void cloneFrom(EntityVillagerTek source) {
        this.func_96094_a(source.func_95999_t());
        while (source.isMale() != this.isMale()) {
            this.func_184221_a(UUID.randomUUID());
        }
        this.setHappy(source.getHappy());
        this.setIntelligence(source.getIntelligence());
        this.setHunger(source.getHunger());
        this.villagerInventory.mergeItems(source.villagerInventory);
        this.bedPos = source.bedPos;
        this.homeFrame = source.homeFrame;
        this.daysAlive = source.daysAlive;
        this.dayCheckTime = source.dayCheckTime;
        source.applySkillsTo(this);
        this.getInventory().cloneFrom(source.getInventory());
        source.func_70106_y();
    }

    protected void applySkillsTo(EntityVillagerTek target) {
        for (ProfessionType pt : SKILLS.keySet()) {
            int skill = this.getBaseSkill(pt);
            if (skill <= target.getBaseSkill(pt) || !pt.canCopy) continue;
            target.setSkill(pt, skill);
        }
    }

    public void func_70014_b(NBTTagCompound compound) {
        super.func_70014_b(compound);
        compound.func_74768_a("happy", this.getHappy());
        compound.func_74768_a("hunger", this.getHunger());
        compound.func_74768_a("intelligence", this.getIntelligence());
        compound.func_74768_a("daysAlive", this.daysAlive);
        compound.func_74768_a("dayCheckTime", this.dayCheckTime);
        for (ProfessionType pt : SKILLS.keySet()) {
            if (!pt.canCopy) continue;
            compound.func_74768_a(pt.name, this.getBaseSkill(pt));
        }
        for (String filterName : this.aiFilters.keySet()) {
            compound.func_74757_a("ai_" + filterName, this.isAIFilterEnabled(filterName));
        }
        compound.func_74757_a("hasHome", this.homeFrame != null);
        if (this.homeFrame != null) {
            this.writeBlockPosNBT(compound, "homeFrame", this.homeFrame);
        }
        compound.func_74783_a("recentEats", this.recentEats.stream().mapToInt(i -> i).toArray());
        this.villagerInventory.writeNBT(compound);
    }

    public void func_70037_a(NBTTagCompound compound) {
        super.func_70037_a(compound);
        this.setHappy(compound.func_74762_e("happy"));
        this.setHunger(compound.func_74762_e("hunger"));
        this.setIntelligence(compound.func_74762_e("intelligence"));
        this.daysAlive = compound.func_74762_e("daysAlive");
        this.dayCheckTime = compound.func_74762_e("dayCheckTime");
        for (ProfessionType pt : SKILLS.keySet()) {
            if (!pt.canCopy) continue;
            this.setSkill(pt, compound.func_74762_e(pt.name));
        }
        for (String filterName : this.aiFilters.keySet()) {
            String key = "ai_" + filterName;
            if (compound.func_74764_b(key)) {
                this.setAIFilter(filterName, compound.func_74767_n("ai_" + filterName));
                continue;
            }
            this.setAIFilter(filterName, true);
        }
        boolean hasHome = compound.func_74767_n("hasHome");
        if (hasHome) {
            this.homeFrame = this.readBlockPosNBT(compound, "homeFrame");
        }
        this.recentEats.clear();
        int[] eats = compound.func_74759_k("recentEats");
        Arrays.stream(eats).forEach(i -> this.recentEats.add(i));
        this.func_189654_d(false);
        this.villagerInventory.readNBT(compound);
        this.getDesireSet().forceUpdate();
    }

    protected BlockPos readBlockPosNBT(NBTTagCompound compound, String key) {
        NBTTagList nbttaglist = compound.func_150295_c(key, 6);
        return new BlockPos(nbttaglist.func_150309_d(0), nbttaglist.func_150309_d(1), nbttaglist.func_150309_d(2));
    }

    protected void writeBlockPosNBT(NBTTagCompound compound, String key, BlockPos val) {
        compound.func_74782_a(key, (NBTBase)this.func_70087_a(new double[]{val.func_177958_n(), val.func_177956_o(), val.func_177952_p()}));
    }

    static {
        for (ProfessionType pt : ProfessionType.values()) {
            SKILLS.put(pt, (DataParameter<Integer>)EntityDataManager.func_187226_a(EntityVillagerTek.class, (DataSerializer)DataSerializers.field_187192_b));
        }
    }

    private static class BedCheckRunnable
    implements Runnable {
        private final WeakReference<EntityVillagerTek> villager;

        public BedCheckRunnable(EntityVillagerTek v) {
            this.villager = new WeakReference<EntityVillagerTek>(v);
        }

        @Override
        public void run() {
            if (this.villager.get() != null) {
                ((EntityVillagerTek)((Object)this.villager.get())).bedCheck();
            }
        }
    }

    private static class GoalRandomizerRunnable
    implements Runnable {
        private final WeakReference<EntityVillagerTek> villager;

        public GoalRandomizerRunnable(EntityVillagerTek v) {
            this.villager = new WeakReference<EntityVillagerTek>(v);
        }

        @Override
        public void run() {
            if (this.villager.get() != null) {
                ((EntityVillagerTek)((Object)this.villager.get())).randomizeGoals();
            }
        }
    }

    private static class CleanUpRunnable
    implements Runnable {
        private final WeakReference<EntityVillagerTek> villager;

        public CleanUpRunnable(EntityVillagerTek v) {
            this.villager = new WeakReference<EntityVillagerTek>(v);
        }

        @Override
        public void run() {
            if (this.villager.get() != null) {
                ((EntityVillagerTek)((Object)this.villager.get())).cleanUpInventory();
            }
        }
    }

    public static enum VillagerThought {
        BED(115, "red_bed.png"),
        HUNGRY(116, "food.png"),
        PICK(117, "iron_pick.png"),
        HOE(118, "iron_hoe.png"),
        AXE(119, "iron_axe.png"),
        SWORD(120, "iron_sword.png"),
        BOOKSHELF(121, "bookshelf.png"),
        PIG_FOOD(122, "pig_carrot.png"),
        SHEEP_FOOD(123, "sheep_wheat.png"),
        COW_FOOD(124, "cow_wheat.png"),
        CHICKEN_FOOD(125, "chicken_seeds.png"),
        BUCKET(126, "bucket.png"),
        SHEARS(127, "shears.png"),
        TAVERN(128, "structure_tavern.png"),
        NOTEBLOCK(129, "noteblock.png"),
        TEACHER(130, "prof_teacher.png"),
        TORCH(131, "torch.png"),
        INSOMNIA(132, "insomnia.png"),
        CROWDED(133, "crowded.png"),
        DO_NOT_USE(999, "meh.png");

        private int numVal;
        private String texture;

        private VillagerThought(int val, String tex) {
            this.numVal = val;
            this.texture = tex;
        }

        public int getVal() {
            return this.numVal;
        }

        public String getTex() {
            return this.texture;
        }

        public float getScale() {
            return 1.0f;
        }

        public static VillagerThought valueOf(int val) {
            for (VillagerThought thought : VillagerThought.values()) {
                if (thought.numVal != val) continue;
                return thought;
            }
            return null;
        }
    }

    public static enum MovementMode {
        WALK((byte)1, 1.0f, "villager_walk"),
        SKIP((byte)2, 1.1f, "villager_skip"),
        RUN((byte)3, 1.4f, "villager_run"),
        SULK((byte)4, 0.7f, "villager_walk_sad");

        public float speedMult;
        public byte id;
        public String anim;

        private MovementMode(byte id, float mult, String anim) {
            this.speedMult = mult;
            this.id = id;
            this.anim = anim;
        }

        public static MovementMode valueOf(byte id) {
            for (MovementMode mode : MovementMode.values()) {
                if (mode.id != id) continue;
                return mode;
            }
            return null;
        }
    }
}


