/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemHoe
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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
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
import net.tangotek.tektopia.entities.ai.EntityAICraftItems;
import net.tangotek.tektopia.entities.ai.EntityAIGatherCane;
import net.tangotek.tektopia.entities.ai.EntityAIHarvestFarm;
import net.tangotek.tektopia.entities.ai.EntityAIPlantFarm;
import net.tangotek.tektopia.entities.ai.EntityAITillDirt;
import net.tangotek.tektopia.entities.crafting.Recipe;
import net.tangotek.tektopia.storage.ItemDesire;
import net.tangotek.tektopia.storage.UpgradeItemDesire;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityFarmer
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityFarmer.class);
    private static final DataParameter<Boolean> HARVEST_WHEAT = EntityDataManager.func_187226_a(EntityFarmer.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> HARVEST_CARROT = EntityDataManager.func_187226_a(EntityFarmer.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> HARVEST_POTATO = EntityDataManager.func_187226_a(EntityFarmer.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> HARVEST_BEETROOT = EntityDataManager.func_187226_a(EntityFarmer.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PLANT_WHEAT = EntityDataManager.func_187226_a(EntityFarmer.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PLANT_CARROT = EntityDataManager.func_187226_a(EntityFarmer.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PLANT_POTATO = EntityDataManager.func_187226_a(EntityFarmer.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> PLANT_BEETROOT = EntityDataManager.func_187226_a(EntityFarmer.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> TILL_DIRT = EntityDataManager.func_187226_a(EntityFarmer.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> GATHER_CANE = EntityDataManager.func_187226_a(EntityFarmer.class, (DataSerializer)DataSerializers.field_187198_h);
    private static List<Recipe> craftSet = EntityFarmer.buildCraftSet();
    private static final Map<String, DataParameter<Boolean>> RECIPE_PARAMS;

    public EntityFarmer(World worldIn) {
        super(worldIn, ProfessionType.FARMER, VillagerRole.VILLAGER.value);
        if (this.field_70170_p.field_72995_K) {
            this.addAnimationTrigger("tektopia:villager_pickup", 47, new Runnable(){

                @Override
                public void run() {
                    EntityFarmer.this.field_70170_p.func_184134_a(EntityFarmer.this.field_70165_t, EntityFarmer.this.field_70163_u, EntityFarmer.this.field_70161_v, SoundEvents.field_187693_cj, SoundCategory.BLOCKS, 1.0f, EntityFarmer.this.field_70146_Z.nextFloat() * 0.4f + 0.8f, false);
                }
            });
        }
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        craftSet.forEach(r -> this.registerAIFilter(r.getAiFilter(), RECIPE_PARAMS.get(r.getAiFilter())));
        this.registerAIFilter("harvest_tile.crops", HARVEST_WHEAT);
        this.registerAIFilter("harvest_tile.carrots", HARVEST_CARROT);
        this.registerAIFilter("harvest_tile.potatoes", HARVEST_POTATO);
        this.registerAIFilter("harvest_tile.beetroots", HARVEST_BEETROOT);
        this.registerAIFilter("plant_tile.crops", PLANT_WHEAT);
        this.registerAIFilter("plant_tile.carrots", PLANT_CARROT);
        this.registerAIFilter("plant_tile.potatoes", PLANT_POTATO);
        this.registerAIFilter("plant_tile.beetroots", PLANT_BEETROOT);
        this.registerAIFilter("till_dirt", TILL_DIRT);
        this.registerAIFilter("gather_cane", GATHER_CANE);
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.getDesireSet().addItemDesire(new ItemDesire(Items.field_151014_N, 1, 8, 16, p -> p.isAIFilterEnabled("plant_tile.crops")));
        this.getDesireSet().addItemDesire(new ItemDesire(Items.field_185163_cU, 1, 8, 16, p -> p.isAIFilterEnabled("plant_tile.beetroots")));
        this.getDesireSet().addItemDesire(new ItemDesire(Items.field_151174_bG, 1, 5, 12, p -> p.isAIFilterEnabled("plant_tile.potatoes")));
        this.getDesireSet().addItemDesire(new ItemDesire(Items.field_151172_bF, 1, 5, 12, p -> p.isAIFilterEnabled("plant_tile.carrots")));
        this.getDesireSet().addItemDesire(new ItemDesire(Items.field_151015_O, 0, 0, 12, null));
        this.getDesireSet().addItemDesire(new ItemDesire(Items.field_185164_cV, 0, 0, 12, null));
        this.getDesireSet().addItemDesire(new UpgradeItemDesire("Hoe", EntityFarmer.getBestHoe(), 1, 1, 1, p -> p.isWorkTime()));
        craftSet.forEach(r -> this.getDesireSet().addRecipeDesire((Recipe)r));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAICraftItems(this, craftSet, "villager_craft", null, 80, VillageStructureType.STORAGE, Blocks.field_150462_ai, p -> p.isWorkTime()));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIPlantFarm(this));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAITillDirt(this, p -> p.isWorkTime()));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIGatherCane(this, 0));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIHarvestFarm(this, false));
    }

    private static List<Recipe> buildCraftSet() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        Recipe recipe = new Recipe(ProfessionType.FARMER, "craft_wooden_hoe", 3, new ItemStack(Items.field_151017_I, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.FARMER, 11, 2), 1, v -> !EntityFarmer.hasHoe(v)){

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
        this.getInventory().func_174894_a(ModItems.makeTaggedItem(new ItemStack(Items.field_151017_I), ItemTagType.VILLAGER));
        return super.func_180482_a(difficulty, livingdata);
    }

    public static Item getSeed(IBlockState blockState) {
        if (blockState.func_177230_c() == Blocks.field_150464_aj) {
            return Items.field_151014_N;
        }
        if (blockState.func_177230_c() == Blocks.field_150469_bN) {
            return Items.field_151174_bG;
        }
        if (blockState.func_177230_c() == Blocks.field_150459_bM) {
            return Items.field_151172_bF;
        }
        if (blockState.func_177230_c() == Blocks.field_185773_cZ) {
            return Items.field_185163_cU;
        }
        return null;
    }

    @Override
    protected ItemStack modifyPickUpStack(ItemStack itemStack) {
        if (itemStack.func_77973_b() == Items.field_151172_bF || itemStack.func_77973_b() == Items.field_151174_bG) {
            int skill = this.getSkill(this.getProfessionType());
            if (itemStack.func_190916_E() >= 2 && this.func_70681_au().nextInt(100) > skill) {
                itemStack.func_190920_e(itemStack.func_190916_E() - 1);
            }
        } else if ((itemStack.func_77973_b() == Items.field_151014_N || itemStack.func_77973_b() == Items.field_185163_cU) && itemStack.func_190916_E() > 1) {
            itemStack.func_190920_e(1);
        }
        return itemStack;
    }

    @Override
    protected boolean canVillagerPickupItem(ItemStack itemIn) {
        return this.isHarvestItem().test(itemIn) || this.isSeed().test(itemIn) || super.canVillagerPickupItem(itemIn);
    }

    protected static boolean hasHoe(EntityVillagerTek villager) {
        List<ItemStack> weaponList = villager.getInventory().getItems(EntityFarmer.getBestHoe(), 1);
        return !weaponList.isEmpty();
    }

    public static Function<ItemStack, Integer> getBestHoe() {
        return p -> {
            if (p.func_77973_b() instanceof ItemHoe) {
                return p.func_77958_k();
            }
            return -1;
        };
    }

    public Predicate<ItemStack> isSeed() {
        return p -> p.func_77973_b() == Items.field_151014_N || p.func_77973_b() == Items.field_185163_cU;
    }

    @Override
    public Predicate<ItemStack> isHarvestItem() {
        return p -> p.func_77973_b() == Items.field_151015_O || p.func_77973_b() == Items.field_151172_bF || p.func_77973_b() == Items.field_185164_cV || p.func_77973_b() == Items.field_151174_bG || p.func_77973_b() == Items.field_151120_aE || super.isHarvestItem().test((ItemStack)p);
    }

    @Override
    protected void cleanUpInventory() {
        super.cleanUpInventory();
    }

    static {
        animHandler.addAnim("tektopia", "villager_pickup", "farmer_m", true);
        animHandler.addAnim("tektopia", "villager_take", "farmer_m", true);
        animHandler.addAnim("tektopia", "villager_hoe", "farmer_m", true);
        animHandler.addAnim("tektopia", "villager_craft", "farmer_m", true);
        EntityVillagerTek.setupAnimations(animHandler, "farmer_m");
        RECIPE_PARAMS = new HashMap<String, DataParameter<Boolean>>();
        craftSet.forEach(r -> RECIPE_PARAMS.put(r.getAiFilter(), (DataParameter<Boolean>)EntityDataManager.func_187226_a(EntityFarmer.class, (DataSerializer)DataSerializers.field_187198_h)));
    }
}

