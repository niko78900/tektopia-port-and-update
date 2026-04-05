/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureAttribute
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.google.common.base.Predicate;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityCaptainAura;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAICraftItems;
import net.tangotek.tektopia.entities.ai.EntityAIEatGoldenApple;
import net.tangotek.tektopia.entities.ai.EntityAIMeleeTarget;
import net.tangotek.tektopia.entities.ai.EntityAIPatrolGuardPost;
import net.tangotek.tektopia.entities.ai.EntityAIPatrolVillage;
import net.tangotek.tektopia.entities.ai.EntityAIPickUpItem;
import net.tangotek.tektopia.entities.ai.EntityAIProtectVillage;
import net.tangotek.tektopia.entities.ai.EntityAISalute;
import net.tangotek.tektopia.entities.ai.EntityAIThorSlam;
import net.tangotek.tektopia.entities.ai.EntityAITrainingTarget;
import net.tangotek.tektopia.entities.crafting.Recipe;
import net.tangotek.tektopia.storage.ItemDesire;
import net.tangotek.tektopia.storage.UpgradeEquipment;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureBarracks;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityGuard
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityGuard.class);
    private static final DataParameter<Boolean> SUPER_ATTACK = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> SALUTE = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> EAT_GOLDEN_APPLE = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PICKUP_EMERALDS = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PATROL_GUARD_POST = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PRACTICE_MELEE = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> EQUIP_LEATHER_ARMOR = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> EQUIP_IRON_ARMOR = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> EQUIP_GOLD_ARMOR = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> EQUIP_DIAMOND_ARMOR = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> EQUIP_ENCHANTED_ARMOR = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> EQUIP_IRON_SWORD = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> EQUIP_DIAMOND_SWORD = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> EQUIP_ENCHANTED_SWORD = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> CAPTAIN = EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h);
    private static List<Recipe> craftSet = EntityGuard.buildCraftSet();
    private static final int[] blockStateIds;
    private static final Map<String, DataParameter<Boolean>> RECIPE_PARAMS;
    private static final Map<Long, Integer> GOLEM_GUARD_ASSIGNMENTS = new HashMap<Long, Integer>();
    private static final float SNOW_GOLEM_DAMAGE = 3.0f;
    private static final double GOLEM_SCAN_RADIUS = 140.0;
    private static final double GOLEM_TARGET_RANGE = 48.0;
    private static final double GOLEM_TOWNHALL_RANGE_SQ = 100.0;
    private static final float IRON_GOLEM_RETREAT_HEALTH = 0.35f;
    private static final float SNOW_GOLEM_RETREAT_HEALTH = 0.45f;
    protected int wantsPractice = 0;
    protected int courageChance = 1;
    private boolean golemScanInitialized = false;

    public EntityGuard(World worldIn) {
        super(worldIn, ProfessionType.GUARD, VillagerRole.VILLAGER.value | VillagerRole.DEFENDER.value);
        this.addAnimationTrigger("tektopia:villager_chop", 48, new Runnable(){

            @Override
            public void run() {
                EntityGuard.this.field_70170_p.func_184134_a(EntityGuard.this.field_70165_t, EntityGuard.this.field_70163_u, EntityGuard.this.field_70161_v, SoundEvents.field_187770_dm, SoundCategory.NEUTRAL, 2.0f, EntityGuard.this.field_70146_Z.nextFloat() * 0.2f + 0.9f, false);
            }
        });
        this.addAnimationTrigger("tektopia:villager_thor_jump", 120, new Runnable(){

            @Override
            public void run() {
                Random rnd = EntityGuard.this.func_70681_au();
                for (int i = 0; i < 50; ++i) {
                    double dx = rnd.nextGaussian();
                    double dz = rnd.nextGaussian();
                    double dy = (double)rnd.nextFloat() * 0.5;
                    double speedX = MathHelper.func_82716_a((Random)rnd, (double)-0.2, (double)0.2);
                    double speedY = rnd.nextDouble();
                    double speedZ = MathHelper.func_82716_a((Random)rnd, (double)-0.2, (double)0.2);
                    EntityGuard.this.field_70170_p.func_175688_a(EnumParticleTypes.BLOCK_DUST, EntityGuard.this.field_70165_t + dx, EntityGuard.this.field_70163_u + dy, EntityGuard.this.field_70161_v + dz, speedX, speedY, speedZ, new int[]{blockStateIds[rnd.nextInt(blockStateIds.length)]});
                }
            }
        });
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        craftSet = this.buildCraftSet();
        Runnable onHit = () -> {
            this.tryAddSkill(ProfessionType.GUARD, this.getMeleeSkillChance());
            if (this.isCaptain() && this.isHostile().test((Entity)this.func_70638_az())) {
                if (this.courageChance > 0 && this.func_70681_au().nextInt(this.courageChance) == 0) {
                    EntityCaptainAura aura = new EntityCaptainAura(this.func_130014_f_(), this.getX(), this.getY(), this.getZ());
                    aura.func_184483_a(3.0f);
                    aura.func_184485_d(10);
                    aura.func_184486_b(40);
                    aura.func_184487_c((float)this.getSkillLerp(ProfessionType.GUARD, 1, 6) / 10.0f);
                    this.field_70170_p.func_72838_d((Entity)aura);
                    this.playSound(ModSoundEvents.courageAura);
                    this.courageChance += 6;
                } else {
                    this.courageChance = Math.max(this.courageChance - 1, 1);
                }
            }
        };
        this.getDesireSet().addItemDesire(new ItemDesire(Items.field_151153_ao, 1, 1, 1, null));
        this.getDesireSet().addItemDesire(new UpgradeEquipment("Weapon", EntityGuard.getBestWeapon(this), EntityEquipmentSlot.MAINHAND, null));
        this.getDesireSet().addItemDesire(new UpgradeEquipment("ArmorFeet", EntityGuard.getBestArmor(this, EntityEquipmentSlot.FEET), EntityEquipmentSlot.FEET, null));
        this.getDesireSet().addItemDesire(new UpgradeEquipment("ArmorHead", EntityGuard.getBestArmor(this, EntityEquipmentSlot.HEAD), EntityEquipmentSlot.HEAD, null));
        this.getDesireSet().addItemDesire(new UpgradeEquipment("ArmorChest", EntityGuard.getBestArmor(this, EntityEquipmentSlot.CHEST), EntityEquipmentSlot.CHEST, null));
        this.getDesireSet().addItemDesire(new UpgradeEquipment("ArmorLegs", EntityGuard.getBestArmor(this, EntityEquipmentSlot.LEGS), EntityEquipmentSlot.LEGS, null));
        craftSet.forEach(r -> this.getDesireSet().addRecipeDesire((Recipe)r));
        this.addTask(49, new EntityAIEatGoldenApple(this));
        this.addTask(49, new EntityAIThorSlam(this, p -> EntityGuard.getWeapon(this), 3.0, p -> !p.isSleepingTime()));
        this.addTask(49, new EntityAIMeleeTarget(this, p -> EntityGuard.getWeapon(this), EntityVillagerTek.VillagerThought.SWORD, p -> !p.isSleepingTime(), onHit, ProfessionType.GUARD));
        this.addTask(50, new EntityAISalute(this));
        this.addTask(50, new EntityAICraftItems(this, craftSet, "villager_craft", null, 80, VillageStructureType.STORAGE, Blocks.field_150462_ai, p -> p.isWorkTime() && !EntityGuard.hasWeapon(this)));
        ArrayList<EntityAIPickUpItem.PickUpData> pickUpCounts = new ArrayList<EntityAIPickUpItem.PickUpData>();
        pickUpCounts.add(new EntityAIPickUpItem.PickUpData(new ItemStack(Items.field_151166_bC, 1, 0), 60, "pickup_emeralds"));
        this.addTask(50, new EntityAIPickUpItem(this, pickUpCounts, 1));
        this.addTask(50, new EntityAIPatrolGuardPost(this, p -> this.hasVillage() && p.isWorkTime() && EntityGuard.hasWeapon(this), 8, 60));
        this.addTask(50, new EntityAIPatrolVillage(this, p -> EntityGuard.hasWeapon(this)));
        this.field_70715_bh.func_75776_a(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.field_70715_bh.func_75776_a(2, (EntityAIBase)new EntityAIProtectVillage(this, p -> !p.isSleeping()));
        this.field_70715_bh.func_75776_a(3, (EntityAIBase)new EntityAITrainingTarget(this, p -> p.isWorkTime() && this.wantsPractice() > 0));
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        craftSet.forEach(r -> this.registerAIFilter(r.getAiFilter(), RECIPE_PARAMS.get(r.getAiFilter())));
        this.registerAIFilter("guard_super_attack", SUPER_ATTACK);
        this.registerAIFilter("salute", SALUTE);
        this.registerAIFilter("eat_golden_apple", EAT_GOLDEN_APPLE);
        this.registerAIFilter("pickup_emeralds", PICKUP_EMERALDS);
        this.registerAIFilter("patrol_guard_post", PATROL_GUARD_POST);
        this.registerAIFilter("practice_melee", PRACTICE_MELEE);
        this.registerAIFilter("equip_leather_armor", EQUIP_LEATHER_ARMOR);
        this.registerAIFilter("equip_iron_armor", EQUIP_IRON_ARMOR);
        this.registerAIFilter("equip_gold_armor", EQUIP_GOLD_ARMOR);
        this.registerAIFilter("equip_diamond_armor", EQUIP_DIAMOND_ARMOR);
        this.registerAIFilter("equip_enchanted_armor", EQUIP_ENCHANTED_ARMOR);
        this.registerAIFilter("equip_iron_sword", EQUIP_IRON_SWORD);
        this.registerAIFilter("equip_diamond_sword", EQUIP_DIAMOND_SWORD);
        this.registerAIFilter("equip_enchanted_sword", EQUIP_ENCHANTED_SWORD);
        this.field_70180_af.func_187214_a(CAPTAIN, false);
    }

    @Override
    public EntityVillagerTek.MovementMode getDefaultMovement() {
        if (this.village.enemySeenRecently()) {
            return EntityVillagerTek.MovementMode.RUN;
        }
        return super.getDefaultMovement();
    }

    private static List<Recipe> buildCraftSet() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        Recipe recipe = new Recipe(ProfessionType.GUARD, "craft_wooden_sword", 3, new ItemStack(Items.field_151041_m, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.GUARD, 12, 3), 1, v -> !EntityGuard.hasWeapon(v)){

            @Override
            public ItemStack craft(EntityVillagerTek villager) {
                ItemStack result = super.craft(villager);
                villager.modifyHappy(-5);
                return result;
            }
        };
        recipes.add(recipe);
        return recipes;
    }

    @Override
    @Nullable
    public IEntityLivingData func_180482_a(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        this.getInventory().func_174894_a(ModItems.createTaggedItem(Items.field_151041_m, ItemTagType.VILLAGER));
        this.equipBestGear();
        return super.func_180482_a(difficulty, livingdata);
    }

    @Override
    protected void setupServerJobs() {
        super.setupServerJobs();
        this.addJob(new TickJob(320, 160, true, () -> this.func_70606_j(this.func_110143_aJ() + 1.0f)));
    }

    @Override
    protected void skillUpdated(ProfessionType pt) {
        if (pt == ProfessionType.GUARD) {
            this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111128_a((double)this.getSkillLerp(ProfessionType.GUARD, 1, 100) / 100.0);
            this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a((double)this.getSkillLerp(ProfessionType.GUARD, 25, 35));
        }
    }

    protected int getMeleeSkillChance() {
        if (this.func_70638_az() instanceof EntityArmorStand) {
            return 4;
        }
        return 1;
    }

    public ITextComponent func_145748_c_() {
        if (this.isCaptain()) {
            TextComponentTranslation textComponent = new TextComponentTranslation("title.captain", new Object[0]);
            textComponent.func_150258_a(" " + super.func_145748_c_().func_150260_c());
            return textComponent;
        }
        return super.func_145748_c_();
    }

    @Override
    protected void addTask(int priority, EntityAIBase task) {
        super.addTask(priority, task);
    }

    @Override
    protected boolean canVillagerPickupItem(ItemStack itemIn) {
        return itemIn.func_77973_b() == Items.field_151166_bC;
    }

    @Override
    public void equipBestGear() {
        this.equipBestGear(EntityEquipmentSlot.CHEST, EntityGuard.getBestArmor(this, EntityEquipmentSlot.CHEST));
        this.equipBestGear(EntityEquipmentSlot.LEGS, EntityGuard.getBestArmor(this, EntityEquipmentSlot.LEGS));
        this.equipBestGear(EntityEquipmentSlot.FEET, EntityGuard.getBestArmor(this, EntityEquipmentSlot.FEET));
        this.equipBestGear(EntityEquipmentSlot.HEAD, EntityGuard.getBestArmor(this, EntityEquipmentSlot.HEAD));
        this.equipBestGear(EntityEquipmentSlot.MAINHAND, EntityGuard.getBestWeapon(this));
    }

    public static Function<ItemStack, Integer> getBestWeapon(EntityGuard guard) {
        return p -> {
            if (p.func_77973_b() instanceof ItemSword) {
                ItemSword sword = (ItemSword)p.func_77973_b();
                if (p.func_77948_v() && !guard.isAIFilterEnabled("equip_enchanted_sword")) {
                    return -1;
                }
                if (sword.func_150932_j().equals(Item.ToolMaterial.DIAMOND.name()) && !guard.isAIFilterEnabled("equip_diamond_sword")) {
                    return -1;
                }
                if (sword.func_150932_j().equals(Item.ToolMaterial.IRON.name()) && !guard.isAIFilterEnabled("equip_iron_sword")) {
                    return -1;
                }
                int score = (int)sword.func_150931_i();
                score = (int)((float)score + EnchantmentHelper.func_152377_a((ItemStack)p, (EnumCreatureAttribute)EnumCreatureAttribute.UNDEFINED));
                ++score;
                score *= 10;
                if (ModItems.isTaggedItem(p, ItemTagType.VILLAGER)) {
                    ++score;
                }
                return score;
            }
            return -1;
        };
    }

    public static Function<ItemStack, Integer> getBestArmor(EntityGuard guard, EntityEquipmentSlot slot) {
        return p -> {
            if (p.func_77973_b() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor)p.func_77973_b();
                if (armor.field_77881_a == slot) {
                    if (p.func_77948_v() && !guard.isAIFilterEnabled("equip_enchanted_armor")) {
                        return -1;
                    }
                    if (armor.func_82812_d() == ItemArmor.ArmorMaterial.DIAMOND && !guard.isAIFilterEnabled("equip_diamond_armor")) {
                        return -1;
                    }
                    if (armor.func_82812_d() == ItemArmor.ArmorMaterial.IRON && !guard.isAIFilterEnabled("equip_iron_armor")) {
                        return -1;
                    }
                    if (armor.func_82812_d() == ItemArmor.ArmorMaterial.GOLD && !guard.isAIFilterEnabled("equip_gold_armor")) {
                        return -1;
                    }
                    if (armor.func_82812_d() == ItemArmor.ArmorMaterial.LEATHER && !guard.isAIFilterEnabled("equip_leather_armor")) {
                        return -1;
                    }
                    int score = armor.func_82812_d().func_78044_b(armor.field_77881_a);
                    return score += EnchantmentHelper.func_77508_a(Arrays.asList(p), (DamageSource)DamageSource.field_76377_j);
                }
            }
            return -1;
        };
    }

    public boolean isCaptain() {
        return (Boolean)this.field_70180_af.func_187225_a(CAPTAIN);
    }

    public void setCaptain(boolean capt) {
        this.field_70180_af.func_187227_b(CAPTAIN, capt);
        if (capt && this.hasVillage()) {
            List<EntityGuard> otherGuards = this.field_70170_p.func_72872_a(EntityGuard.class, this.getVillage().getAABB().func_186662_g(50.0));
            otherGuards.stream().filter(g -> g.isCaptain() && g != this && g.getVillage() == this.getVillage()).forEach(g -> g.setCaptain(false));
        }
    }

    public static boolean hasWeapon(EntityVillagerTek villager) {
        return !EntityGuard.getWeapon(villager).func_190926_b();
    }

    protected boolean hasPracticeTarget() {
        return this.func_70638_az() instanceof EntityArmorStand;
    }

    protected static ItemStack getWeapon(EntityVillagerTek villager) {
        return villager.func_184582_a(EntityEquipmentSlot.MAINHAND);
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e).func_111128_a(1.0);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.32);
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(1.0);
    }

    @Override
    public boolean canConvertProfession(ProfessionType pt) {
        if (pt == ProfessionType.CAPTAIN && !this.isCaptain()) {
            return true;
        }
        return super.canConvertProfession(pt);
    }

    @Override
    public void func_70604_c(@Nullable EntityLivingBase livingBase) {
        double distRevenge;
        double distTarget;
        super.func_70604_c(livingBase);
        if (livingBase != null && this.func_70638_az() != null && this.func_70638_az() != livingBase && !(livingBase instanceof EntityPlayer) && (distTarget = this.func_70068_e((Entity)this.func_70638_az())) > (distRevenge = this.func_70068_e((Entity)livingBase)) * 2.0) {
            this.func_70624_b(livingBase);
        }
    }

    public void func_70624_b(@Nullable EntityLivingBase target) {
        super.func_70624_b(target);
        if (this.hasVillage() && !(target instanceof EntityArmorStand)) {
            this.getVillage().addActiveDefender(this);
        }
    }

    @Override
    protected void randomizeGoals() {
        super.randomizeGoals();
        this.wantsPractice = 0;
        if (this.func_70681_au().nextInt(2) == 0) {
            this.wantsPractice = this.func_70681_au().nextInt(10) + 15;
        }
    }

    @Override
    public void attachToVillage(Village v) {
        super.attachToVillage(v);
        this.sleepOffset = v.getNextGuardSleepOffset();
    }

    public int wantsPractice() {
        if (this.getSkill(ProfessionType.GUARD) < this.getIntelligence()) {
            return this.wantsPractice;
        }
        return 0;
    }

    @Override
    protected void bedCheck() {
        VillageStructure struct;
        if (this.hasVillage() && this.homeFrame != null && !((struct = this.village.getStructureFromFrame(this.homeFrame)) instanceof VillageStructureBarracks)) {
            List<VillageStructure> barracks = this.village.getStructures(VillageStructureType.BARRACKS);
            VillageStructureBarracks availBarracks = barracks.stream().map(VillageStructureBarracks.class::cast).filter(b -> !b.isFull()).findAny().orElse(null);
            if (availBarracks != null) {
                this.clearHome();
            }
        }
        super.bedCheck();
    }

    @Override
    public void onStopSleep() {
        super.onStopSleep();
        this.equipBestGear();
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
        if (!this.field_70170_p.field_72995_K && this.hasVillage() && this.isGolemCoordinator() && this.field_70173_aa % 5 == 0) {
            this.coordinateVillageGolems();
        }
    }

    private boolean isGolemCoordinator() {
        AxisAlignedBB guardBounds;
        if (!this.hasVillage() || (guardBounds = this.getVillageGuardScanBounds()) == null) {
            return false;
        }
        if (this.isCaptain()) {
            return true;
        }
        List<EntityGuard> guards = this.field_70170_p.func_72872_a(EntityGuard.class, guardBounds);
        boolean captainExists = false;
        int lowestId = this.func_145782_y();
        for (EntityGuard guard : guards) {
            if (guard.getVillage() != this.getVillage() || !guard.func_70089_S()) continue;
            if (guard.isCaptain()) {
                captainExists = true;
                break;
            }
            lowestId = Math.min(lowestId, guard.func_145782_y());
        }
        return !captainExists && this.func_145782_y() == lowestId;
    }

    private void coordinateVillageGolems() {
        AxisAlignedBB scan = this.getGolemScanBounds();
        if (scan == null) {
            return;
        }
        this.assignVillageGolems(scan);
        List<EntityIronGolem> ironGolems = this.field_70170_p.func_72872_a(EntityIronGolem.class, scan);
        for (EntityIronGolem golem : ironGolems) {
            if (!this.isGolemAssignedToThisGuard((EntityCreature)golem)) continue;
            this.updateGolemBehavior((EntityCreature)golem, 1.0);
        }
        List<EntitySnowman> snowGolems = this.field_70170_p.func_72872_a(EntitySnowman.class, scan);
        for (EntitySnowman golem : snowGolems) {
            if (!this.isGolemAssignedToThisGuard((EntityCreature)golem)) continue;
            this.updateGolemBehavior((EntityCreature)golem, 1.15);
            this.clearSnowTrail(golem);
        }
        this.applySnowGolemDamageBoost(scan);
    }

    private AxisAlignedBB getGolemScanBounds() {
        if (this.hasVillage() && this.village.getAABB() != null) {
            return this.village.getAABB().func_186662_g(20.0);
        }
        if (this.hasVillage() && this.village.getOrigin() != null) {
            return new AxisAlignedBB(this.village.getOrigin()).func_186662_g(140.0);
        }
        return this.func_174813_aQ().func_72314_b(GOLEM_SCAN_RADIUS, 30.0, GOLEM_SCAN_RADIUS);
    }

    private AxisAlignedBB getVillageGuardScanBounds() {
        if (!this.hasVillage()) {
            return null;
        }
        if (this.village.getAABB() != null) {
            return this.village.getAABB().func_186662_g(32.0);
        }
        if (this.village.getOrigin() != null) {
            return new AxisAlignedBB(this.village.getOrigin()).func_186662_g(140.0);
        }
        return this.func_174813_aQ().func_72314_b(80.0, 30.0, 80.0);
    }

    private void assignVillageGolems(AxisAlignedBB scan) {
        int guardId = this.func_145782_y();
        boolean initialScan = !this.golemScanInitialized;
        this.golemScanInitialized = true;
        this.cleanupStaleGolemAssignments();
        List<EntityIronGolem> ironGolems = this.field_70170_p.func_72872_a(EntityIronGolem.class, scan);
        for (EntityIronGolem golem : ironGolems) {
            this.assignGolemToGuard((EntityCreature)golem, guardId, initialScan);
        }
        List<EntitySnowman> snowGolems = this.field_70170_p.func_72872_a(EntitySnowman.class, scan);
        for (EntitySnowman golem : snowGolems) {
            this.assignGolemToGuard((EntityCreature)golem, guardId, initialScan);
        }
    }

    private void assignGolemToGuard(EntityCreature golem, int guardId, boolean initialScan) {
        if (golem == null || !golem.func_70089_S()) {
            return;
        }
        long key = this.getGolemAssignmentKey((Entity)golem);
        Integer currentGuard = GOLEM_GUARD_ASSIGNMENTS.get(key);
        if (currentGuard == null) {
            GOLEM_GUARD_ASSIGNMENTS.put(key, guardId);
            return;
        }
        if (currentGuard != guardId && initialScan) {
            GOLEM_GUARD_ASSIGNMENTS.put(key, guardId);
        } else if (currentGuard != guardId && !this.isGuardControllerValid(currentGuard)) {
            GOLEM_GUARD_ASSIGNMENTS.put(key, guardId);
        }
    }

    private boolean isGuardControllerValid(int guardId) {
        Entity controller = this.field_70170_p.func_73045_a(guardId);
        if (!(controller instanceof EntityGuard) || !controller.func_70089_S()) {
            return false;
        }
        EntityGuard guard = (EntityGuard)controller;
        return guard.hasVillage() && this.hasVillage() && guard.getVillage() == this.getVillage();
    }

    private boolean isGolemAssignedToThisGuard(EntityCreature golem) {
        if (golem == null) {
            return false;
        }
        Integer guardId = GOLEM_GUARD_ASSIGNMENTS.get(this.getGolemAssignmentKey((Entity)golem));
        return guardId != null && guardId == this.func_145782_y();
    }

    private void cleanupStaleGolemAssignments() {
        long worldKey = (long)System.identityHashCode(this.field_70170_p) & 0xFFFFFFFFL;
        GOLEM_GUARD_ASSIGNMENTS.entrySet().removeIf(entry -> {
            if ((entry.getKey() >> 32 & 0xFFFFFFFFL) != worldKey) {
                return false;
            }
            int entityId = (int)(entry.getKey().longValue() & 0xFFFFFFFFL);
            Entity existing = this.field_70170_p.func_73045_a(entityId);
            return !(existing instanceof EntityIronGolem) && !(existing instanceof EntitySnowman) || !existing.func_70089_S();
        });
    }

    private long getGolemAssignmentKey(Entity golem) {
        long worldKey = (long)System.identityHashCode(golem.field_70170_p) & 0xFFFFFFFFL;
        long entityId = (long)golem.func_145782_y() & 0xFFFFFFFFL;
        return worldKey << 32 | entityId;
    }

    private void updateGolemBehavior(EntityCreature golem, double speed) {
        if (golem == null || !golem.func_70089_S()) {
            return;
        }
        EntityLivingBase target = this.findGolemTarget(golem);
        if (target != null) {
            golem.func_70624_b(target);
            if (!(golem instanceof EntitySnowman)) {
                golem.func_70661_as().func_75497_a((Entity)target, speed);
            } else {
                golem.func_70661_as().func_75499_g();
            }
            return;
        }
        if (this.shouldGolemRetreat(golem) || this.shouldReturnGolemToTownHall(golem)) {
            golem.func_70624_b(null);
            this.sendGolemToTownHall(golem, speed + 0.1);
        }
    }

    private EntityLivingBase findGolemTarget(EntityLivingBase golem) {
        AxisAlignedBB searchArea;
        if (this.hasVillage() && this.village.getAABB() != null) {
            searchArea = this.village.getAABB().func_186662_g(20.0);
        } else {
            searchArea = golem.func_174813_aQ().func_72314_b(GOLEM_TARGET_RANGE, 16.0, GOLEM_TARGET_RANGE);
        }
        List<EntityLivingBase> enemies = this.field_70170_p.func_175647_a(EntityLivingBase.class, searchArea, e -> e != golem && this.isGolemHostile((EntityLivingBase)e));
        EntityLivingBase minionTarget = null;
        EntityLivingBase enemyTarget = null;
        double minionDist = Double.MAX_VALUE;
        double enemyDist = Double.MAX_VALUE;
        for (EntityLivingBase enemy : enemies) {
            double dist = golem.func_70068_e((Entity)enemy);
            if (EntityNecromancer.isMinion(enemy)) {
                if (!(dist < minionDist)) continue;
                minionDist = dist;
                minionTarget = enemy;
                continue;
            }
            if (!(dist < enemyDist)) continue;
            enemyDist = dist;
            enemyTarget = enemy;
        }
        if (minionTarget != null) {
            return minionTarget;
        }
        return enemyTarget;
    }

    private boolean isGolemHostile(EntityLivingBase target) {
        if (target == null || !target.func_70089_S()) {
            return false;
        }
        return this.isHostile().test((Entity)target) || EntityNecromancer.isMinion(target);
    }

    private boolean shouldGolemRetreat(EntityLivingBase golem) {
        float maxHealth = golem.func_110138_aP();
        if (maxHealth <= 0.0f) {
            return false;
        }
        float pct = golem.func_110143_aJ() / maxHealth;
        if (golem instanceof EntitySnowman) {
            return pct <= SNOW_GOLEM_RETREAT_HEALTH;
        }
        return pct <= IRON_GOLEM_RETREAT_HEALTH;
    }

    private boolean shouldReturnGolemToTownHall(EntityCreature golem) {
        return !Village.isNightTime(this.field_70170_p) && golem.func_70638_az() == null;
    }

    private void sendGolemToTownHall(EntityCreature golem, double speed) {
        BlockPos townHallPos = this.getTownHallGuardPoint();
        if (townHallPos == null || golem.func_174818_b(townHallPos) <= GOLEM_TOWNHALL_RANGE_SQ) {
            return;
        }
        golem.func_70661_as().func_75492_a((double)townHallPos.func_177958_n() + 0.5, (double)townHallPos.func_177956_o(), (double)townHallPos.func_177952_p() + 0.5, speed);
    }

    private BlockPos getTownHallGuardPoint() {
        if (!this.hasVillage()) {
            return null;
        }
        VillageStructure townHall = this.village.getNearestStructure(VillageStructureType.TOWNHALL, this.village.getOrigin());
        if (townHall != null) {
            return townHall.getDoorOutside(2);
        }
        return this.village.getOrigin();
    }

    private void applySnowGolemDamageBoost(AxisAlignedBB scan) {
        List<EntitySnowball> snowballs = this.field_70170_p.func_72872_a(EntitySnowball.class, scan);
        for (EntitySnowball snowball : snowballs) {
            Entity thrower = snowball.func_85052_h();
            if (!(thrower instanceof EntitySnowman) || !thrower.func_70089_S()) continue;
            List<EntityLivingBase> impacted = this.field_70170_p.func_175647_a(EntityLivingBase.class, snowball.func_174813_aQ().func_72314_b(0.65, 0.65, 0.65), e -> this.canSnowballDamageTarget((EntityLivingBase)e));
            if (impacted.isEmpty()) continue;
            EntityLivingBase target = impacted.stream().min((a, b) -> Double.compare(a.func_70068_e((Entity)snowball), b.func_70068_e((Entity)snowball))).orElse(null);
            if (target == null) continue;
            target.func_70097_a(DamageSource.func_76356_a((Entity)snowball, thrower), SNOW_GOLEM_DAMAGE);
            this.field_70170_p.func_72960_a((Entity)snowball, (byte)3);
            snowball.func_70106_y();
        }
    }

    private boolean canSnowballDamageTarget(EntityLivingBase target) {
        if (target == null || target instanceof EntityVillagerTek || target instanceof EntityVillager) {
            return false;
        }
        return this.isGolemHostile(target);
    }

    private void clearSnowTrail(EntitySnowman golem) {
        int xMin = MathHelper.func_76128_c((double)(golem.field_70165_t - 0.25));
        int xMax = MathHelper.func_76128_c((double)(golem.field_70165_t + 0.25));
        int zMin = MathHelper.func_76128_c((double)(golem.field_70161_v - 0.25));
        int zMax = MathHelper.func_76128_c((double)(golem.field_70161_v + 0.25));
        int y = MathHelper.func_76128_c((double)golem.field_70163_u);
        for (int x = xMin; x <= xMax; ++x) {
            for (int z = zMin; z <= zMax; ++z) {
                BlockPos pos = new BlockPos(x, y, z);
                if (this.field_70170_p.func_180495_p(pos).func_177230_c() != Blocks.field_150431_aC) continue;
                this.field_70170_p.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
            }
        }
    }

    protected boolean canVillagerPickupItem(Item itemIn) {
        return false;
    }

    public Predicate<Entity> isSuitableTarget() {
        return e -> super.isHostile().test(e) || e instanceof EntityArmorStand;
    }

    public boolean func_70652_k(Entity entityIn) {
        return this.attackEntityAsMob(entityIn, 1.0);
    }

    public boolean attackEntityAsMob(Entity entityIn, double knockbackModifier) {
        --this.wantsPractice;
        float damage = (float)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
        damage *= (float)this.getSkillLerp(ProfessionType.GUARD, 100, 200) / 100.0f;
        double knockback = 0.0;
        if (entityIn instanceof EntityLivingBase) {
            damage += EnchantmentHelper.func_152377_a((ItemStack)this.func_184614_ca(), (EnumCreatureAttribute)((EntityLivingBase)entityIn).func_70668_bt());
            knockback += (double)EnchantmentHelper.func_77501_a((EntityLivingBase)this);
            knockback += (double)this.getSkillLerp(ProfessionType.GUARD, 1, 5) / 10.0;
            knockback *= knockbackModifier;
        }
        this.debugOut("Attacking " + entityIn.func_145748_c_().func_150260_c() + " | " + damage + " damage, " + knockback + " knock");
        boolean flag = entityIn.func_70097_a(DamageSource.func_76358_a((EntityLivingBase)this), damage);
        if (flag) {
            int j;
            if (knockback > 0.0 && entityIn instanceof EntityLivingBase) {
                ((EntityLivingBase)entityIn).func_70653_a((Entity)this, (float)knockback, (double)MathHelper.func_76126_a((float)(this.field_70177_z * ((float)Math.PI / 180))), (double)(-MathHelper.func_76134_b((float)(this.field_70177_z * ((float)Math.PI / 180)))));
                this.field_70159_w *= 0.6;
                this.field_70179_y *= 0.6;
            }
            if ((j = EnchantmentHelper.func_90036_a((EntityLivingBase)this)) > 0) {
                entityIn.func_70015_d(j * 4);
            }
            this.func_174815_a((EntityLivingBase)this, entityIn);
        }
        return flag;
    }

    @Override
    public boolean isFleeFrom(Entity e) {
        ItemStack equipped = this.func_184582_a(EntityEquipmentSlot.MAINHAND);
        if (equipped == ItemStack.field_190927_a) {
            return super.isFleeFrom(e);
        }
        return false;
    }

    @Override
    public void func_70014_b(NBTTagCompound compound) {
        super.func_70014_b(compound);
        compound.func_74757_a("captain", this.isCaptain());
    }

    @Override
    public void func_70037_a(NBTTagCompound compound) {
        super.func_70037_a(compound);
        this.setCaptain(compound.func_74767_n("captain"));
    }

    static {
        int[] nArray = new int[4];
        nArray[0] = Block.func_176210_f((IBlockState)Blocks.field_150346_d.func_176223_P());
        nArray[1] = Block.func_176210_f((IBlockState)Blocks.field_150346_d.func_176223_P());
        nArray[2] = Block.func_176210_f((IBlockState)Blocks.field_150348_b.func_176223_P());
        nArray[3] = Block.func_176210_f((IBlockState)Blocks.field_150347_e.func_176223_P());
        blockStateIds = nArray;
        RECIPE_PARAMS = new HashMap<String, DataParameter<Boolean>>();
        craftSet.forEach(r -> RECIPE_PARAMS.put(r.getAiFilter(), (DataParameter<Boolean>)EntityDataManager.func_187226_a(EntityGuard.class, (DataSerializer)DataSerializers.field_187198_h)));
        animHandler.addAnim("tektopia", "villager_chop", "guard_m", true);
        animHandler.addAnim("tektopia", "villager_thor_jump", "guard_m", false);
        animHandler.addAnim("tektopia", "villager_salute", "guard_m", true);
        animHandler.addAnim("tektopia", "villager_craft", "guard_m", false);
        EntityVillagerTek.setupAnimations(animHandler, "guard_m");
    }
}


