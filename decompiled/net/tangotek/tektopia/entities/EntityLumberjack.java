/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIChopTree;
import net.tangotek.tektopia.entities.ai.EntityAICraftItems;
import net.tangotek.tektopia.entities.ai.EntityAIPickUpItem;
import net.tangotek.tektopia.entities.crafting.Recipe;
import net.tangotek.tektopia.storage.ItemDesireNoDeliver;
import net.tangotek.tektopia.storage.UpgradeItemDesire;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityLumberjack
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityLumberjack.class);
    private static final DataParameter<Boolean> CHOP_TREE_OAK = EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> CHOP_TREE_BIRCH = EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> CHOP_TREE_JUNGLE = EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> CHOP_TREE_SPRUCE = EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PICK_UP_OAK_SAPLINGS = EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PICK_UP_SPRUCE_SAPLINGS = EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PICK_UP_BIRCH_SAPLINGS = EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PICK_UP_JUNGLE_SAPLINGS = EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PICK_UP_ACACIA_SAPLINGS = EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PICK_UP_DARK_OAK_SAPLINGS = EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PICK_UP_APPLES = EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h);
    private static List<Recipe> craftSet = EntityLumberjack.buildCraftSet();
    private static final Map<String, DataParameter<Boolean>> RECIPE_PARAMS;

    public EntityLumberjack(World worldIn) {
        super(worldIn, ProfessionType.LUMBERJACK, VillagerRole.VILLAGER.value);
        if (this.field_70170_p.field_72995_K) {
            this.addAnimationTrigger("tektopia:villager_chop", 48, new Runnable(){

                @Override
                public void run() {
                    EntityLumberjack.this.field_70170_p.func_184134_a(EntityLumberjack.this.field_70165_t, EntityLumberjack.this.field_70163_u, EntityLumberjack.this.field_70161_v, SoundEvents.field_187889_gU, SoundCategory.BLOCKS, 2.0f, EntityLumberjack.this.field_70146_Z.nextFloat() * 0.2f + 0.9f, false);
                }
            });
        }
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    @Nullable
    public IEntityLivingData func_180482_a(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        this.getInventory().func_174894_a(new ItemStack(Item.func_150898_a((Block)Blocks.field_150345_g), 16, BlockPlanks.EnumType.OAK.func_176839_a()));
        ItemStack axe = ModItems.makeTaggedItem(new ItemStack(Items.field_151053_p), ItemTagType.VILLAGER);
        this.getInventory().func_174894_a(axe);
        return super.func_180482_a(difficulty, livingdata);
    }

    protected static boolean hasAxe(EntityVillagerTek villager) {
        List<ItemStack> weaponList = villager.getInventory().getItems(EntityLumberjack.getBestAxe(villager), 1);
        return !weaponList.isEmpty();
    }

    public static Function<ItemStack, Integer> getBestAxe(EntityVillagerTek villager) {
        return p -> {
            if (p.func_77973_b() instanceof ItemAxe) {
                return 50 - EntityAIChopTree.getChopCount(villager, p);
            }
            return -1;
        };
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        craftSet.forEach(r -> this.registerAIFilter(r.getAiFilter(), RECIPE_PARAMS.get(r.getAiFilter())));
        this.registerAIFilter("chop_tree_oak", CHOP_TREE_OAK);
        this.registerAIFilter("chop_tree_birch", CHOP_TREE_BIRCH);
        this.registerAIFilter("chop_tree_jungle", CHOP_TREE_JUNGLE);
        this.registerAIFilter("chop_tree_spruce", CHOP_TREE_SPRUCE);
        this.registerAIFilter("pickup_oak_saplings", PICK_UP_OAK_SAPLINGS);
        this.registerAIFilter("pickup_spruce_saplings", PICK_UP_SPRUCE_SAPLINGS);
        this.registerAIFilter("pickup_birch_saplings", PICK_UP_BIRCH_SAPLINGS);
        this.registerAIFilter("pickup_jungle_saplings", PICK_UP_JUNGLE_SAPLINGS);
        this.registerAIFilter("pickup_acacia_saplings", PICK_UP_ACACIA_SAPLINGS);
        this.registerAIFilter("pickup_dark_oak_saplings", PICK_UP_DARK_OAK_SAPLINGS);
        this.registerAIFilter("pickup_apples", PICK_UP_APPLES);
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.getDesireSet().addItemDesire(new UpgradeItemDesire("Axe", EntityLumberjack.getBestAxe(this), 1, 1, 1, p -> p.isWorkTime()));
        this.getDesireSet().addItemDesire(new ItemDesireNoDeliver("Sapling 0", EntityLumberjack.isSapling(0), 0, 10, 60, null));
        this.getDesireSet().addItemDesire(new ItemDesireNoDeliver("Sapling 1", EntityLumberjack.isSapling(1), 0, 10, 60, null));
        this.getDesireSet().addItemDesire(new ItemDesireNoDeliver("Sapling 2", EntityLumberjack.isSapling(2), 0, 10, 60, null));
        this.getDesireSet().addItemDesire(new ItemDesireNoDeliver("Sapling 3", EntityLumberjack.isSapling(3), 0, 10, 60, null));
        this.getDesireSet().addItemDesire(new ItemDesireNoDeliver("Sapling 4", EntityLumberjack.isSapling(4), 0, 10, 60, null));
        this.getDesireSet().addItemDesire(new ItemDesireNoDeliver("Sapling 5", EntityLumberjack.isSapling(5), 0, 10, 60, null));
        craftSet.forEach(r -> this.getDesireSet().addRecipeDesire((Recipe)r));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAICraftItems(this, craftSet, "villager_craft", null, 80, VillageStructureType.STORAGE, Blocks.field_150462_ai, p -> p.isWorkTime()));
        ArrayList<EntityAIPickUpItem.PickUpData> pickUpCounts = new ArrayList<EntityAIPickUpItem.PickUpData>();
        pickUpCounts.add(new EntityAIPickUpItem.PickUpData(new ItemStack(Item.func_150898_a((Block)Blocks.field_150345_g), 1, 0), 60, "pickup_oak_saplings"));
        pickUpCounts.add(new EntityAIPickUpItem.PickUpData(new ItemStack(Item.func_150898_a((Block)Blocks.field_150345_g), 1, 1), 60, "pickup_spruce_saplings"));
        pickUpCounts.add(new EntityAIPickUpItem.PickUpData(new ItemStack(Item.func_150898_a((Block)Blocks.field_150345_g), 1, 2), 60, "pickup_birch_saplings"));
        pickUpCounts.add(new EntityAIPickUpItem.PickUpData(new ItemStack(Item.func_150898_a((Block)Blocks.field_150345_g), 1, 3), 60, "pickup_jungle_saplings"));
        pickUpCounts.add(new EntityAIPickUpItem.PickUpData(new ItemStack(Item.func_150898_a((Block)Blocks.field_150345_g), 1, 4), 60, "pickup_acacia_saplings"));
        pickUpCounts.add(new EntityAIPickUpItem.PickUpData(new ItemStack(Item.func_150898_a((Block)Blocks.field_150345_g), 1, 5), 60, "pickup_dark_oak_saplings"));
        pickUpCounts.add(new EntityAIPickUpItem.PickUpData(new ItemStack(Items.field_151034_e, 1), 10, "pickup_apples"));
        this.addTask(50, new EntityAIPickUpItem(this, pickUpCounts, 1));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIChopTree(this, true));
    }

    private static List<Recipe> buildCraftSet() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        Recipe recipe = new Recipe(ProfessionType.LUMBERJACK, "craft_wooden_axe", 3, new ItemStack(Items.field_151053_p, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.LUMBERJACK, 11, 2), 1, v -> !EntityLumberjack.hasAxe(v)){

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
    public void func_70636_d() {
        super.func_70636_d();
    }

    @Override
    protected boolean canVillagerPickupItem(ItemStack itemIn) {
        return this.isHarvestItem().test(itemIn) || EntityLumberjack.isSapling().test(itemIn) || itemIn.func_77973_b() == Items.field_151034_e || super.canVillagerPickupItem(itemIn);
    }

    public static Predicate<ItemStack> isSapling() {
        return p -> p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150345_g);
    }

    public static Predicate<ItemStack> isSapling(int type) {
        return p -> EntityLumberjack.isSapling().test((ItemStack)p) && p.func_77960_j() == type;
    }

    public Predicate<EntityVillagerTek> hasAxe() {
        return p -> !p.getInventory().getItem(EntityLumberjack.getBestAxe(this)).func_190926_b();
    }

    @Override
    public Predicate<ItemStack> isHarvestItem() {
        return p -> p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150364_r) || p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150363_s) || super.isHarvestItem().test((ItemStack)p);
    }

    @Override
    protected ItemStack modifyPickUpStack(ItemStack itemStack) {
        if (itemStack.func_77973_b() == Items.field_151034_e) {
            ModItems.makeTaggedItem(itemStack, ItemTagType.VILLAGER);
        }
        return itemStack;
    }

    static {
        animHandler.addAnim("tektopia", "villager_chop", "lumberjack_m", true);
        animHandler.addAnim("tektopia", "villager_craft", "lumberjack_m", true);
        EntityVillagerTek.setupAnimations(animHandler, "lumberjack_m");
        RECIPE_PARAMS = new HashMap<String, DataParameter<Boolean>>();
        craftSet.forEach(r -> RECIPE_PARAMS.put(r.getAiFilter(), (DataParameter<Boolean>)EntityDataManager.func_187226_a(EntityLumberjack.class, (DataSerializer)DataSerializers.field_187198_h)));
    }
}

