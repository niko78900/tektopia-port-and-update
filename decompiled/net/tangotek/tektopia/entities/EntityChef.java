/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
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
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAICraftItems;
import net.tangotek.tektopia.entities.ai.EntityAIEmptyFurnace;
import net.tangotek.tektopia.entities.ai.EntityAISmelting;
import net.tangotek.tektopia.entities.crafting.Recipe;
import net.tangotek.tektopia.storage.ItemDesire;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityChef
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityChef.class);
    private static final DataParameter<Boolean> COOK_BEEF = EntityDataManager.func_187226_a(EntityChef.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> COOK_MUTTON = EntityDataManager.func_187226_a(EntityChef.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> COOK_CHICKEN = EntityDataManager.func_187226_a(EntityChef.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> COOK_PORK = EntityDataManager.func_187226_a(EntityChef.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> COOK_POTATO = EntityDataManager.func_187226_a(EntityChef.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> SMELT_CHARCOAL = EntityDataManager.func_187226_a(EntityChef.class, (DataSerializer)DataSerializers.field_187198_h);
    private static List<Recipe> craftSet = EntityChef.buildCraftSet();
    private static final Map<String, DataParameter<Boolean>> RECIPE_PARAMS = new HashMap<String, DataParameter<Boolean>>();

    public EntityChef(World worldIn) {
        super(worldIn, ProfessionType.CHEF, VillagerRole.VILLAGER.value);
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.registerAIFilter("cook_beef", COOK_BEEF);
        this.registerAIFilter("cook_pork", COOK_PORK);
        this.registerAIFilter("cook_mutton", COOK_MUTTON);
        this.registerAIFilter("cook_chicken", COOK_CHICKEN);
        this.registerAIFilter("cook_potato", COOK_POTATO);
        this.registerAIFilter("smelt_charcoal", SMELT_CHARCOAL);
        craftSet.forEach(r -> this.registerAIFilter(r.getAiFilter(), RECIPE_PARAMS.get(r.getAiFilter())));
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.getDesireSet().addItemDesire(new ItemDesire("Fuel", EntityAISmelting.getBestFuel(), 1, 6, 10, null));
        this.getDesireSet().addItemDesire(new ItemDesire(Blocks.field_150364_r, 0, 4, 8, p -> p.isAIFilterEnabled("smelt_charcoal")));
        this.getDesireSet().addItemDesire(new ItemDesire("Cookable", EntityChef.bestCookable(this), 0, this.getSkillLerp(ProfessionType.CHEF, 1, 3) * 8, 0, null));
        craftSet.forEach(r -> this.getDesireSet().addRecipeDesire((Recipe)r));
        this.addTask(50, new EntityAIEmptyFurnace(this, VillageStructureType.KITCHEN, EntityChef.takeFromFurnace()));
        this.addTask(50, new EntityAISmelting((EntityVillagerTek)this, VillageStructureType.KITCHEN, p -> !EntityChef.hasCoal(p, 1) && p.isAIFilterEnabled("smelt_charcoal"), p -> p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150364_r) ? 1 : 0, () -> this.tryAddSkill(ProfessionType.CHEF, 10)));
        this.addTask(50, new EntityAISmelting((EntityVillagerTek)this, VillageStructureType.KITCHEN, p -> true, EntityChef.bestCookable(this), () -> this.tryAddSkill(ProfessionType.CHEF, 3)));
        this.addTask(50, new EntityAICraftItems(this, craftSet, "villager_cook", null, 80, VillageStructureType.KITCHEN, Blocks.field_150462_ai, p -> p.isWorkTime()));
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
    }

    private static List<Recipe> buildCraftSet() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        ArrayList<Object> ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Items.field_151043_k, 8));
        ingredients.add(new ItemStack(Items.field_151034_e, 1));
        Recipe recipe = new Recipe(ProfessionType.CHEF, "craft_golden_apple", 2, new ItemStack(Items.field_151153_ao, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.CHEF, 7, 2), 16);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151043_k, 1));
        ingredients.add(new ItemStack(Items.field_151172_bF, 1));
        recipe = new Recipe(ProfessionType.CHEF, "craft_golden_carrot", 2, new ItemStack(Items.field_151150_bK, 1), ingredients, 3, 3, v -> v.getSkillLerp(ProfessionType.CHEF, 7, 2), 16);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_185164_cV, 6));
        ingredients.add(new ItemStack(Items.field_151054_z, 1));
        recipe = new Recipe(ProfessionType.CHEF, "craft_beetroot_soup", 2, new ItemStack(Items.field_185165_cW, 1), ingredients, 2, 3, v -> v.getSkillLerp(ProfessionType.CHEF, 7, 2), 15);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151117_aB, 3));
        ingredients.add(new ItemStack(Items.field_151102_aT, 2));
        ingredients.add(new ItemStack(Items.field_151110_aK, 1));
        ingredients.add(new ItemStack(Items.field_151015_O, 3));
        recipe = new Recipe(ProfessionType.CHEF, "craft_cake", 1, new ItemStack(Items.field_151105_aU, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.CHEF, 10, 5), 1){

            @Override
            public ItemStack craft(EntityVillagerTek villager) {
                List<ItemStack> items = villager.getInventory().getItems(p -> p.func_77973_b() == Items.field_151117_aB, 3);
                int villagerMilks = (int)items.stream().filter(itemStack -> ModItems.isTaggedItem(itemStack, ItemTagType.VILLAGER)).count();
                ItemStack result = super.craft(villager);
                if (result != null) {
                    int nonVillagerMilks = 3 - villagerMilks;
                    if (villagerMilks > 0) {
                        villager.getInventory().func_174894_a(ModItems.makeTaggedItem(new ItemStack(Items.field_151133_ar, villagerMilks), ItemTagType.VILLAGER));
                    }
                    if (nonVillagerMilks > 0) {
                        villager.getInventory().func_174894_a(new ItemStack(Items.field_151133_ar, nonVillagerMilks));
                    }
                }
                return result;
            }
        };
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151015_O, 3));
        recipe = new Recipe(ProfessionType.CHEF, "craft_bread", 6, new ItemStack(Items.field_151025_P, 1), ingredients, 3, 5, v -> v.getSkillLerp(ProfessionType.CHEF, 5, 2), 32);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        Predicate<EntityVillagerTek> pred = v -> !Recipe.hasPersonalGoal(v, new ItemStack(Items.field_151054_z, 3));
        Recipe recipe2 = new Recipe(ProfessionType.CHEF, "craft_wooden_bowl", 10, new ItemStack(Items.field_151054_z, 1), ingredients, 3, 3, v -> v.getSkillLerp(ProfessionType.CHEF, 5, 1), 3, pred);
        recipes.add(recipe2);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151120_aE, 1));
        pred = v -> !Recipe.hasPersonalGoal(v, new ItemStack(Items.field_151102_aT, 6));
        recipe2 = new Recipe(ProfessionType.CHEF, "craft_sugar", 15, new ItemStack(Items.field_151102_aT, 1), ingredients, 3, 4, v -> v.getSkillLerp(ProfessionType.CHEF, 3, 1), 32, pred);
        recipes.add(recipe2);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150423_aK), 1));
        ingredients.add(new ItemStack(Items.field_151102_aT, 1));
        ingredients.add(new ItemStack(Items.field_151110_aK, 1));
        recipe = new Recipe(ProfessionType.CHEF, "craft_pumpkin_pie", 2, new ItemStack(Items.field_151158_bO, 1), ingredients, 3, 4, v -> v.getSkillLerp(ProfessionType.CHEF, 9, 4), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151015_O, 2));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BROWN.func_176767_b()));
        recipe = new Recipe(ProfessionType.CHEF, "craft_cookie", 4, new ItemStack(Items.field_151106_aX, 1), ingredients, 3, 3, v -> v.getSkillLerp(ProfessionType.CHEF, 6, 2), 3);
        recipes.add(recipe);
        return recipes;
    }

    private static Predicate<ItemStack> takeFromFurnace() {
        return p -> EntityChef.isCooked().test((ItemStack)p) || p.func_77973_b() == Items.field_151044_h;
    }

    private static Predicate<ItemStack> isCooked() {
        return p -> p.func_77973_b() == Items.field_151077_bg || p.func_77973_b() == Items.field_151083_be || p.func_77973_b() == Items.field_179557_bn || p.func_77973_b() == Items.field_151157_am || p.func_77973_b() == Items.field_151168_bH;
    }

    protected static boolean hasCoal(EntityVillagerTek villager, int req) {
        int count = villager.getInventory().getItemCount(EntityChef.isCoal());
        return count >= req;
    }

    public static Predicate<ItemStack> isCoal() {
        return p -> p.func_77973_b() == Items.field_151044_h;
    }

    private static Function<ItemStack, Integer> bestCookable(EntityVillagerTek villager) {
        return p -> {
            if (p.func_77973_b() == Items.field_151082_bd && villager.isAIFilterEnabled("cook_beef")) {
                return EntityVillagerTek.foodItemValue(null).apply(Items.field_151083_be);
            }
            if (p.func_77973_b() == Items.field_151147_al && villager.isAIFilterEnabled("cook_pork")) {
                return EntityVillagerTek.foodItemValue(null).apply(Items.field_151157_am);
            }
            if (p.func_77973_b() == Items.field_151076_bf && villager.isAIFilterEnabled("cook_chicken")) {
                return EntityVillagerTek.foodItemValue(null).apply(Items.field_151077_bg);
            }
            if (p.func_77973_b() == Items.field_179561_bm && villager.isAIFilterEnabled("cook_mutton")) {
                return EntityVillagerTek.foodItemValue(null).apply(Items.field_179557_bn);
            }
            if (p.func_77973_b() == Items.field_151174_bG && villager.isAIFilterEnabled("cook_potato")) {
                return EntityVillagerTek.foodItemValue(null).apply(Items.field_151168_bH);
            }
            return -1;
        };
    }

    static {
        craftSet.forEach(r -> RECIPE_PARAMS.put(r.getAiFilter(), (DataParameter<Boolean>)EntityDataManager.func_187226_a(EntityChef.class, (DataSerializer)DataSerializers.field_187198_h)));
        animHandler.addAnim("tektopia", "villager_take", "chef_m", false);
        animHandler.addAnim("tektopia", "villager_cook", "chef_m", false);
        EntityVillagerTek.setupAnimations(animHandler, "chef_m");
    }
}

