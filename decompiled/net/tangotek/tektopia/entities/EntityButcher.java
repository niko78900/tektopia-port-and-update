/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityChicken
 *  net.minecraft.entity.passive.EntityCow
 *  net.minecraft.entity.passive.EntityPig
 *  net.minecraft.entity.passive.EntitySheep
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemTool
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.tangotek.tektopia.EntityTagType;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIAttachLeadToButcherAnimal;
import net.tangotek.tektopia.entities.ai.EntityAICraftItems;
import net.tangotek.tektopia.entities.ai.EntityAIKillInStructure;
import net.tangotek.tektopia.entities.ai.EntityAILeadAnimalToStructure;
import net.tangotek.tektopia.entities.ai.EntityAIMeleeTarget;
import net.tangotek.tektopia.entities.crafting.Recipe;
import net.tangotek.tektopia.storage.UpgradeItemDesire;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityButcher
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityButcher.class);
    private static final DataParameter<Boolean> RETRIEVE_CHICKENS = EntityDataManager.func_187226_a(EntityButcher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> RETRIEVE_COWS = EntityDataManager.func_187226_a(EntityButcher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> RETRIEVE_PIGS = EntityDataManager.func_187226_a(EntityButcher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> RETRIEVE_SHEEP = EntityDataManager.func_187226_a(EntityButcher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> BUTCHER_ANIMALS = EntityDataManager.func_187226_a(EntityButcher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static List<Recipe> craftSet = EntityButcher.buildCraftSet();
    private static final Map<String, DataParameter<Boolean>> RECIPE_PARAMS;

    public EntityButcher(World worldIn) {
        super(worldIn, ProfessionType.BUTCHER, VillagerRole.VILLAGER.value);
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e).func_111128_a(5.0);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        craftSet.forEach(r -> this.registerAIFilter(r.getAiFilter(), RECIPE_PARAMS.get(r.getAiFilter())));
        this.registerAIFilter("retrieve_chickens", RETRIEVE_CHICKENS);
        this.registerAIFilter("retrieve_cows", RETRIEVE_COWS);
        this.registerAIFilter("retrieve_pigs", RETRIEVE_PIGS);
        this.registerAIFilter("retrieve_sheep", RETRIEVE_SHEEP);
        this.registerAIFilter("butcher_animals", BUTCHER_ANIMALS);
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.getDesireSet().addItemDesire(new UpgradeItemDesire("Axe", EntityButcher.getBestAxe(), 1, 1, 1, p -> p.isWorkTime()));
        craftSet.forEach(r -> this.getDesireSet().addRecipeDesire((Recipe)r));
        Runnable onHit = () -> {
            this.tryAddSkill(ProfessionType.BUTCHER, 8);
            this.modifyHunger(-5);
        };
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIMeleeTarget(this, p -> EntityButcher.getWeapon(this), EntityVillagerTek.VillagerThought.AXE, p -> p.isWorkTime() && p.isAIFilterEnabled("butcher_animals"), onHit, ProfessionType.BUTCHER));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAILeadAnimalToStructure(this, VillageStructureType.BUTCHER, EntityTagType.BUTCHERED));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIAttachLeadToButcherAnimal(this, p -> p.isWorkTime()));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAICraftItems(this, craftSet, "villager_craft", null, 80, VillageStructureType.BUTCHER, Blocks.field_150462_ai, p -> p.isWorkTime()));
        this.field_70715_bh.func_75776_a(1, (EntityAIBase)new EntityAIKillInStructure(this, VillageStructureType.BUTCHER, EntityAnimal.class, EntityButcher.isTarget(), p -> p.isWorkTime()));
    }

    @Override
    @Nullable
    public IEntityLivingData func_180482_a(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        this.getInventory().func_174894_a(ModItems.makeTaggedItem(new ItemStack(Items.field_151053_p), ItemTagType.VILLAGER));
        return super.func_180482_a(difficulty, livingdata);
    }

    public static Predicate<EntityLivingBase> isTarget() {
        return p -> p instanceof EntitySheep || p instanceof EntityCow || p instanceof EntityChicken || p instanceof EntityPig;
    }

    public static Function<ItemStack, Integer> getBestAxe() {
        return p -> {
            if (p.func_77973_b() instanceof ItemAxe) {
                int score = p.func_77958_k() * 10;
                if (ModItems.isTaggedItem(p, ItemTagType.VILLAGER)) {
                    ++score;
                }
                return score;
            }
            return -1;
        };
    }

    private static List<Recipe> buildCraftSet() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        ArrayList<Object> ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Item.func_150898_a((Block)Blocks.field_150364_r), 1, 99));
        Recipe recipe = new Recipe(ProfessionType.BUTCHER, "craft_wooden_axe", 3, new ItemStack(Items.field_151053_p, 1), ingredients, 1, 1, v -> 12 - v.getSkillLerp(ProfessionType.BUTCHER, 1, 10), 1, p -> EntityButcher.getWeapon(p).func_190926_b()){

            @Override
            public ItemStack craft(EntityVillagerTek villager) {
                ItemStack result = super.craft(villager);
                villager.modifyHappy(-5);
                return result;
            }
        };
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151116_aA, 8));
        recipe = new Recipe(ProfessionType.BUTCHER, "craft_leather_chestplate", 2, new ItemStack((Item)Items.field_151027_R, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BUTCHER, 8, 4), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151116_aA, 7));
        recipe = new Recipe(ProfessionType.BUTCHER, "craft_leather_leggings", 2, new ItemStack((Item)Items.field_151026_S, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BUTCHER, 7, 3), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151116_aA, 4));
        recipe = new Recipe(ProfessionType.BUTCHER, "craft_leather_boots", 3, new ItemStack((Item)Items.field_151021_T, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BUTCHER, 4, 2), 1);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151116_aA, 5));
        recipe = new Recipe(ProfessionType.BUTCHER, "craft_leather_helmet", 3, new ItemStack((Item)Items.field_151024_Q, 1), ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.BUTCHER, 5, 3), 1);
        recipes.add(recipe);
        return recipes;
    }

    private static int getToolBonusValue(ItemTool tool) {
        if (tool != null) {
            if (tool.func_77861_e().equals("STONE")) {
                return 10;
            }
            if (tool.func_77861_e().equals("IRON")) {
                return 30;
            }
            if (tool.func_77861_e().equals("DIAMOND")) {
                return 45;
            }
        }
        return 0;
    }

    public void func_70074_a(EntityLivingBase entityLivingIn) {
        int bonusMeat = 0;
        int extraChance = this.getSkill(ProfessionType.BUTCHER);
        ItemStack tool = EntityButcher.getWeapon(this);
        if (!tool.func_190926_b()) {
            extraChance += EntityButcher.getToolBonusValue((ItemTool)tool.func_77973_b());
        }
        if (this.func_70681_au().nextInt(100) < extraChance) {
            ++bonusMeat;
        }
        if (this.func_70681_au().nextInt(100) < extraChance) {
            ++bonusMeat;
        }
        Item meat = null;
        SoundEvent soundEvent = null;
        if (entityLivingIn instanceof EntityCow) {
            meat = Items.field_151082_bd;
            soundEvent = SoundEvents.field_187560_al;
        } else if (entityLivingIn instanceof EntityPig) {
            meat = Items.field_151147_al;
            soundEvent = SoundEvents.field_187700_dM;
        } else if (entityLivingIn instanceof EntitySheep) {
            meat = Items.field_179561_bm;
            soundEvent = SoundEvents.field_187759_eH;
        } else if (entityLivingIn instanceof EntityChicken) {
            meat = Items.field_151076_bf;
            soundEvent = SoundEvents.field_187663_X;
        }
        if (soundEvent != null) {
            this.func_184185_a(soundEvent, 2.0f, this.func_70647_i());
        }
        if (bonusMeat > 0 && this.field_70170_p.func_82736_K().func_82766_b("doMobLoot")) {
            ItemStack meatStack = new ItemStack(meat, bonusMeat);
            if (ModEntities.isTaggedEntity((Entity)entityLivingIn, EntityTagType.VILLAGER)) {
                ModItems.makeTaggedItem(meatStack, ItemTagType.VILLAGER);
            }
            entityLivingIn.func_70099_a(meatStack, (float)bonusMeat);
        }
        super.func_70074_a(entityLivingIn);
    }

    public static ItemStack getWeapon(EntityVillagerTek villager) {
        List<ItemStack> weaponList = villager.getInventory().getItems(EntityButcher.getBestAxe(), 1);
        if (!weaponList.isEmpty()) {
            return weaponList.get(0);
        }
        return ItemStack.field_190927_a;
    }

    public Predicate<ItemStack> isMeat() {
        return p -> p.func_77973_b() == Items.field_151082_bd || p.func_77973_b() == Items.field_151147_al || p.func_77973_b() == Items.field_179561_bm || p.func_77973_b() == Items.field_151076_bf;
    }

    public Predicate<ItemStack> isAnimalProduct() {
        return p -> p.func_77973_b() == Items.field_151008_G || p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150325_L) || p.func_77973_b() == Items.field_151110_aK || p.func_77973_b() == Items.field_151116_aA;
    }

    @Override
    public Predicate<ItemStack> isHarvestItem() {
        return p -> this.isMeat().test((ItemStack)p) || this.isAnimalProduct().test((ItemStack)p);
    }

    public boolean func_70652_k(Entity entityIn) {
        float damage = (float)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
        boolean flag = entityIn.func_70097_a(DamageSource.func_76358_a((EntityLivingBase)this), damage);
        return flag;
    }

    @Override
    protected boolean canVillagerPickupItem(ItemStack itemIn) {
        return this.isHarvestItem().test(itemIn) || super.canVillagerPickupItem(itemIn);
    }

    static {
        animHandler.addAnim("tektopia", "villager_take", "butcher_m", false);
        animHandler.addAnim("tektopia", "villager_chop", "butcher_m", false);
        animHandler.addAnim("tektopia", "villager_craft", "butcher_m", false);
        EntityVillagerTek.setupAnimations(animHandler, "butcher_m");
        RECIPE_PARAMS = new HashMap<String, DataParameter<Boolean>>();
        craftSet.forEach(r -> RECIPE_PARAMS.put(r.getAiFilter(), (DataParameter<Boolean>)EntityDataManager.func_187226_a(EntityButcher.class, (DataSerializer)DataSerializers.field_187198_h)));
    }
}

