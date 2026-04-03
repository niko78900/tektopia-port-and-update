/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAICraftItems;
import net.tangotek.tektopia.entities.ai.EntityAIEmptyFurnace;
import net.tangotek.tektopia.entities.ai.EntityAIMining;
import net.tangotek.tektopia.entities.ai.EntityAISmelting;
import net.tangotek.tektopia.entities.crafting.Recipe;
import net.tangotek.tektopia.storage.ItemDesire;
import net.tangotek.tektopia.storage.UpgradeItemDesire;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityMiner
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityMiner.class);
    private static final DataParameter<Boolean> SMELT_CHARCOAL = EntityDataManager.func_187226_a(EntityMiner.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> MINING = EntityDataManager.func_187226_a(EntityMiner.class, (DataSerializer)DataSerializers.field_187198_h);
    private static List<Recipe> craftSet = EntityMiner.buildCraftSet();
    private boolean isUnderground = false;
    private static final Map<String, DataParameter<Boolean>> RECIPE_PARAMS;

    public EntityMiner(World worldIn) {
        super(worldIn, ProfessionType.MINER, VillagerRole.VILLAGER.value);
        this.addAnimationTrigger("tektopia:villager_chop", 47, new Runnable(){

            @Override
            public void run() {
                EntityMiner.this.field_70170_p.func_184134_a(EntityMiner.this.field_70165_t, EntityMiner.this.field_70163_u, EntityMiner.this.field_70161_v, SoundEvents.field_187843_fX, SoundCategory.BLOCKS, 1.0f, EntityMiner.this.field_70146_Z.nextFloat() * 0.4f + 0.8f, false);
            }
        });
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void setupServerJobs() {
        super.setupServerJobs();
        this.addJob(new TickJob(20, 20, true, () -> this.updateUnderground()));
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        craftSet.forEach(r -> this.registerAIFilter(r.getAiFilter(), RECIPE_PARAMS.get(r.getAiFilter())));
        this.registerAIFilter("smelt_charcoal", SMELT_CHARCOAL);
        this.registerAIFilter("mining", MINING);
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        VillageStructureType[] smeltBuildings = new VillageStructureType[]{VillageStructureType.BLACKSMITH, VillageStructureType.STORAGE};
        Predicate<ItemStack> isLapis = p -> p.func_77973_b() == Items.field_151100_aR && p.func_77960_j() == EnumDyeColor.BLUE.func_176767_b();
        this.getDesireSet().addItemDesire(new ItemDesire(Blocks.field_150478_aa, 10, 20, 30, null));
        this.getDesireSet().addItemDesire(new ItemDesire(Blocks.field_150364_r, 0, 3, 10, p -> !EntityMiner.hasTorches(p, 10) && p.isAIFilterEnabled("smelt_charcoal")));
        this.getDesireSet().addItemDesire(new UpgradeItemDesire("Pick", EntityMiner.getBestPick(this), 1, 1, 1, p -> p.isWorkTime()));
        this.getDesireSet().addItemDesire(new ItemDesire(Items.field_151045_i, 0, 0, 5, null));
        this.getDesireSet().addItemDesire(new ItemDesire(Blocks.field_150366_p, 0, 0, 8, null));
        this.getDesireSet().addItemDesire(new ItemDesire(Blocks.field_150352_o, 0, 0, 8, null));
        this.getDesireSet().addItemDesire(new ItemDesire(Items.field_151137_ax, 0, 0, 16, null));
        this.getDesireSet().addItemDesire(new ItemDesire("Lapis", isLapis, 0, 0, 16, null));
        craftSet.forEach(r -> this.getDesireSet().addRecipeDesire((Recipe)r));
        this.addTask(50, new EntityAICraftItems(this, craftSet, "villager_craft", null, 80, VillageStructureType.STORAGE, Blocks.field_150462_ai, p -> p.isWorkTime()));
        this.addTask(50, new EntityAIEmptyFurnace(this, smeltBuildings, EntityMiner.isSmeltProduct(), p -> !EntityMiner.hasCoal(p, 8)));
        this.addTask(50, new EntityAISmelting((EntityVillagerTek)this, smeltBuildings, p -> !EntityMiner.hasTorches(p, 10) && !EntityMiner.hasCoal(p, 8) && p.isAIFilterEnabled("smelt_charcoal"), EntityMiner.bestSmeltable(), () -> this.tryAddSkill(ProfessionType.MINER, 7)));
        this.addTask(50, new EntityAIMining(this, p -> p.isWorkTime()));
    }

    @Override
    public boolean isDeliveryTime() {
        return Village.isTimeOfDay(this.field_70170_p, WORK_START_TIME - 2000, WORK_END_TIME, this.sleepOffset) && !this.field_70170_p.func_72896_J();
    }

    private static List<Recipe> buildCraftSet() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        ArrayList<Object> ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        Recipe recipe = new Recipe(ProfessionType.MINER, "craft_wooden_pickaxe", 3, new ItemStack(Items.field_151039_o, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.MINER, 11, 2), 1, v -> !EntityMiner.hasPick(v)){

            @Override
            public ItemStack craft(EntityVillagerTek villager) {
                ItemStack result = super.craft(villager);
                villager.modifyHappy(-5);
                return result;
            }
        };
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        ingredients.add(new ItemStack(Items.field_151044_h, 8));
        recipe = new Recipe(ProfessionType.MINER, "craft_charcoal_torch", 7, new ItemStack(Item.func_150898_a((Block)Blocks.field_150478_aa), 32), ingredients, 1, 1, v -> 8 - v.getSkillLerp(ProfessionType.MINER, 1, 6), 64, v -> !EntityMiner.hasTorches(v, 10));
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        ingredients.add(new ItemStack(Items.field_151044_h, 8, 1));
        recipe = new Recipe(ProfessionType.MINER, "craft_coal_torch", 7, new ItemStack(Item.func_150898_a((Block)Blocks.field_150478_aa), 32), ingredients, 1, 1, v -> 8 - v.getSkillLerp(ProfessionType.MINER, 1, 6), 64, v -> !EntityMiner.hasTorches(v, 10));
        recipes.add(recipe);
        return recipes;
    }

    @Override
    @Nullable
    public IEntityLivingData func_180482_a(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        this.getInventory().func_174894_a(ModItems.createTaggedItem(Items.field_151039_o, ItemTagType.VILLAGER));
        this.getInventory().func_174894_a(ModItems.createTaggedItem(Item.func_150898_a((Block)Blocks.field_150478_aa), 16, ItemTagType.VILLAGER));
        return super.func_180482_a(difficulty, livingdata);
    }

    protected void updateUnderground() {
        BlockPos pos = this.func_180425_c();
        this.isUnderground = pos.func_177956_o() < 62 && !this.field_70170_p.func_175678_i(pos);
    }

    @Override
    protected void func_82167_n(Entity entityIn) {
        VillageStructure struct;
        if (entityIn instanceof EntityVillagerTek && this.hasVillage() && (struct = this.getVillage().getStructure(this.func_180425_c())) != null && struct.type == VillageStructureType.MINESHAFT) {
            return;
        }
        super.func_82167_n(entityIn);
    }

    @Override
    public void addVillagerPosition() {
    }

    @Override
    protected boolean canVillagerPickupItem(ItemStack itemIn) {
        return this.isHarvestItem().test(itemIn);
    }

    protected static boolean hasTorches(EntityVillagerTek villager, int req) {
        int count = villager.getInventory().getItemCount(EntityMiner.isTorch());
        return count >= req;
    }

    protected static boolean hasCoal(EntityVillagerTek villager, int req) {
        int count = villager.getInventory().getItemCount(EntityMiner.isCoal());
        return count >= req;
    }

    protected static boolean hasPick(EntityVillagerTek villager) {
        List<ItemStack> weaponList = villager.getInventory().getItems(EntityMiner.getBestPick(villager), 1);
        return !weaponList.isEmpty();
    }

    public static Predicate<ItemStack> isCoal() {
        return p -> p.func_77973_b() == Items.field_151044_h;
    }

    public static Predicate<ItemStack> isTorch() {
        return p -> p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150478_aa);
    }

    public static Function<ItemStack, Integer> getBestPick(EntityVillagerTek villager) {
        return p -> {
            if (p.func_77973_b() instanceof ItemPickaxe) {
                return 50 - EntityAIMining.getSwingCount(villager, p);
            }
            return -1;
        };
    }

    @Override
    public float func_70689_ay() {
        float speed = super.func_70689_ay();
        if (this.isUnderground) {
            speed *= (float)this.getSkillLerp(ProfessionType.MINER, 100, 140) / 100.0f;
        }
        return speed;
    }

    @Override
    public Predicate<ItemStack> isHarvestItem() {
        return p -> p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150366_p) || p.func_77973_b() == Items.field_151044_h || p.func_77973_b() == Items.field_151100_aR || p.func_77973_b() == Items.field_151045_i || p.func_77973_b() == Items.field_151137_ax || p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150352_o) || p.func_77973_b() == Items.field_151166_bC || super.isHarvestItem().test((ItemStack)p);
    }

    private static Predicate<ItemStack> isSmeltProduct() {
        return p -> p.func_77973_b() == Items.field_151044_h;
    }

    private static Function<ItemStack, Integer> bestSmeltable() {
        return p -> p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150364_r) ? 1 : 0;
    }

    static {
        animHandler.addAnim("tektopia", "villager_chop", "miner_m", true);
        animHandler.addAnim("tektopia", "villager_craft", "miner_m", true);
        EntityVillagerTek.setupAnimations(animHandler, "miner_m");
        RECIPE_PARAMS = new HashMap<String, DataParameter<Boolean>>();
        craftSet.forEach(r -> RECIPE_PARAMS.put(r.getAiFilter(), (DataParameter<Boolean>)EntityDataManager.func_187226_a(EntityMiner.class, (DataSerializer)DataSerializers.field_187198_h)));
    }
}

