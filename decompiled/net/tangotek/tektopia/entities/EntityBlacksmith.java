/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  net.minecraft.block.Block
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.SoundCategory
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
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
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

public class EntityBlacksmith
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityBlacksmith.class);
    private static final DataParameter<Boolean> SMELT_IRON = EntityDataManager.func_187226_a(EntityBlacksmith.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> SMELT_GOLD = EntityDataManager.func_187226_a(EntityBlacksmith.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> SMELT_CHARCOAL = EntityDataManager.func_187226_a(EntityBlacksmith.class, (DataSerializer)DataSerializers.field_187198_h);
    private static List<Recipe> craftSetAnvil = EntityBlacksmith.buildCraftSetAnvil();
    private static List<Recipe> craftSetBench = EntityBlacksmith.buildCraftSetBench();
    private static final Map<String, DataParameter<Boolean>> RECIPE_PARAMS = new HashMap<String, DataParameter<Boolean>>();

    public EntityBlacksmith(World worldIn) {
        super(worldIn, ProfessionType.BLACKSMITH, VillagerRole.VILLAGER.value);
        this.addAnimationTrigger("tektopia:villager_hammer", 47, new Runnable(){

            @Override
            public void run() {
                EntityBlacksmith.this.field_70170_p.func_184134_a(EntityBlacksmith.this.field_70165_t, EntityBlacksmith.this.field_70163_u, EntityBlacksmith.this.field_70161_v, SoundEvents.field_187692_g, SoundCategory.BLOCKS, 1.0f + EntityBlacksmith.this.field_70146_Z.nextFloat() * 0.5f, EntityBlacksmith.this.field_70146_Z.nextFloat() * 0.4f + 0.8f, false);
            }
        });
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        craftSetAnvil.forEach(r -> this.registerAIFilter(r.getAiFilter(), RECIPE_PARAMS.get(r.getAiFilter())));
        craftSetBench.forEach(r -> this.registerAIFilter(r.getAiFilter(), RECIPE_PARAMS.get(r.getAiFilter())));
        this.registerAIFilter("smelt_iron", SMELT_IRON);
        this.registerAIFilter("smelt_gold", SMELT_GOLD);
        this.registerAIFilter("smelt_charcoal", SMELT_CHARCOAL);
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.getDesireSet().addItemDesire(new ItemDesire("Fuel", EntityAISmelting.getBestFuel(), 1, 6, 10, null));
        this.getDesireSet().addItemDesire(new ItemDesire(Blocks.field_150366_p, 0, 8, 16, p -> p.isAIFilterEnabled("smelt_iron")));
        this.getDesireSet().addItemDesire(new ItemDesire(Blocks.field_150352_o, 0, 8, 16, p -> p.isAIFilterEnabled("smelt_gold")));
        this.getDesireSet().addItemDesire(new ItemDesire(Blocks.field_150364_r, 0, 4, 8, p -> p.isAIFilterEnabled("smelt_charcoal")));
        craftSetAnvil.forEach(r -> this.getDesireSet().addRecipeDesire((Recipe)r));
        craftSetBench.forEach(r -> this.getDesireSet().addRecipeDesire((Recipe)r));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIEmptyFurnace(this, VillageStructureType.BLACKSMITH, EntityBlacksmith.isSmelted()));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAICraftItems(this, craftSetAnvil, "villager_hammer", new ItemStack((Item)ModItems.ironHammer, 1), 60, VillageStructureType.BLACKSMITH, Blocks.field_150467_bQ, p -> p.isWorkTime()));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAICraftItems(this, craftSetBench, "villager_craft", null, 80, VillageStructureType.BLACKSMITH, Blocks.field_150462_ai, p -> p.isWorkTime()));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAISmelting((EntityVillagerTek)this, VillageStructureType.BLACKSMITH, p -> true, EntityBlacksmith.bestSmeltable(this), () -> this.tryAddSkill(ProfessionType.BLACKSMITH, 4)));
        this.addTask(50, new EntityAISmelting((EntityVillagerTek)this, VillageStructureType.BLACKSMITH, p -> !EntityBlacksmith.hasCoal(p, 1) && p.isAIFilterEnabled("smelt_charcoal"), p -> p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150364_r) ? 1 : 0, () -> this.tryAddSkill(ProfessionType.BLACKSMITH, 10)));
    }

    private static List<Recipe> buildCraftSetAnvil() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        ArrayList<Object> ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Items.field_151045_i, 2));
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        Recipe recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_diamond_sword", 1, new ItemStack(Items.field_151048_u, 1), ingredients, 2, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 9, 3), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151045_i, 4));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_diamond_boots", 1, new ItemStack((Item)Items.field_151175_af, 1), ingredients, 2, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 9, 3), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151045_i, 8));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_diamond_chestplate", 1, new ItemStack((Item)Items.field_151163_ad, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 12, 5), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151045_i, 7));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_diamond_leggings", 1, new ItemStack((Item)Items.field_151173_ae, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 10, 4), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151045_i, 5));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_diamond_helmet", 1, new ItemStack((Item)Items.field_151161_ac, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 8, 3), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151045_i, 3));
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_diamond_axe", 1, new ItemStack(Items.field_151056_x, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 9, 3), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151045_i, 3));
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_diamond_pickaxe", 1, new ItemStack(Items.field_151046_w, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 9, 3), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151042_j, 3));
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_iron_axe", 2, new ItemStack(Items.field_151036_c, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 9, 3), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151042_j, 3));
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_iron_pickaxe", 2, new ItemStack(Items.field_151035_b, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 9, 3), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151042_j, 2));
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_iron_sword", 2, new ItemStack(Items.field_151040_l, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 9, 3), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151042_j, 2));
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_iron_hoe", 2, new ItemStack(Items.field_151019_K, 1), ingredients, 2, 3, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 9, 3), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151042_j, 3));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_bucket", 3, new ItemStack(Items.field_151133_ar, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 6, 2), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151042_j, 2));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_shears", 3, new ItemStack((Item)Items.field_151097_aZ, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 6, 2), 2);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151042_j, 4));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_iron_boots", 2, new ItemStack((Item)Items.field_151167_ab, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 6, 2), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151042_j, 8));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_iron_chestplate", 1, new ItemStack((Item)Items.field_151030_Z, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 12, 5), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151042_j, 7));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_iron_leggings", 1, new ItemStack((Item)Items.field_151165_aa, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 10, 4), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151042_j, 5));
        recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_iron_helmet", 2, new ItemStack((Item)Items.field_151028_Y, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 8, 3), 1);
        recipes.add(recipe);
        return recipes;
    }

    private static List<Recipe> buildCraftSetBench() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        ingredients.add(new ItemStack(Items.field_151044_h, 8));
        Recipe recipe = new Recipe(ProfessionType.BLACKSMITH, "craft_torch", 9, new ItemStack(Item.func_150898_a((Block)Blocks.field_150478_aa), 32), ingredients, 1, 8, v -> v.getSkillLerp(ProfessionType.BLACKSMITH, 5, 2), 64);
        recipes.add(recipe);
        return recipes;
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
    }

    @Override
    protected void cleanUpInventory() {
        super.cleanUpInventory();
    }

    protected Predicate<ItemStack> isDeliverable() {
        return p -> craftSetAnvil.stream().anyMatch(e -> ItemStack.func_179545_c((ItemStack)e.getProduct(), (ItemStack)p) || p.func_77973_b() == Items.field_151043_k);
    }

    @Override
    protected boolean canVillagerPickupItem(ItemStack itemIn) {
        return this.isDeliverable().test(itemIn) || itemIn.func_77973_b() == Items.field_151044_h || itemIn.func_77973_b() == Items.field_151042_j || itemIn.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150366_p) || super.canVillagerPickupItem(itemIn);
    }

    private static Predicate<ItemStack> isSmelted() {
        return p -> p.func_77973_b() == Items.field_151042_j || p.func_77973_b() == Items.field_151043_k || p.func_77973_b() == Items.field_151044_h;
    }

    protected static boolean hasCoal(EntityVillagerTek villager, int req) {
        int count = villager.getInventory().getItemCount(EntityBlacksmith.isCoal());
        return count >= req;
    }

    public static Predicate<ItemStack> isCoal() {
        return p -> p.func_77973_b() == Items.field_151044_h;
    }

    private static Function<ItemStack, Integer> bestSmeltable(EntityVillagerTek villager) {
        return p -> {
            if (p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150366_p) && villager.isAIFilterEnabled("smelt_iron")) {
                return 3;
            }
            if (p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150352_o) && villager.isAIFilterEnabled("smelt_gold")) {
                return 2;
            }
            return 0;
        };
    }

    static {
        craftSetAnvil.forEach(r -> RECIPE_PARAMS.put(r.getAiFilter(), (DataParameter<Boolean>)EntityDataManager.func_187226_a(EntityBlacksmith.class, (DataSerializer)DataSerializers.field_187198_h)));
        craftSetBench.forEach(r -> RECIPE_PARAMS.put(r.getAiFilter(), (DataParameter<Boolean>)EntityDataManager.func_187226_a(EntityBlacksmith.class, (DataSerializer)DataSerializers.field_187198_h)));
        animHandler.addAnim("tektopia", "villager_hammer", "blacksmith_m", true);
        animHandler.addAnim("tektopia", "villager_craft", "blacksmith_m", true);
        EntityVillagerTek.setupAnimations(animHandler, "blacksmith_m");
    }
}

