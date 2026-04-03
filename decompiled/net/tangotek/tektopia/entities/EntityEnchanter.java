/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Enchantments
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.google.common.base.Optional;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAICraftItems;
import net.tangotek.tektopia.entities.ai.EntityAIEnchant;
import net.tangotek.tektopia.entities.crafting.Recipe;
import net.tangotek.tektopia.storage.ItemDesire;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityEnchanter
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityEnchanter.class);
    protected static final DataParameter<Optional<BlockPos>> SPELL_BLOCK = EntityDataManager.func_187226_a(EntityEnchanter.class, (DataSerializer)DataSerializers.field_187201_k);
    private static List<Recipe> enchantSet = EntityEnchanter.buildEnchantSet();
    private static List<Recipe> craftSet = EntityEnchanter.buildCraftSet();
    private static final Map<String, DataParameter<Boolean>> RECIPE_PARAMS = new HashMap<String, DataParameter<Boolean>>();
    private int blockParticleTick = 0;
    private int handParticleTick = 0;
    private BlockPos clientSpellBlock;

    public EntityEnchanter(World worldIn) {
        super(worldIn, ProfessionType.ENCHANTER, VillagerRole.VILLAGER.value);
        Runnable enchantSound = () -> this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, ModSoundEvents.villagerEnchant, SoundCategory.NEUTRAL, 1.5f, this.field_70146_Z.nextFloat() * 0.2f + 0.9f, false);
        Runnable enchantApplySound = () -> this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, ModSoundEvents.villagerEnchantApply, SoundCategory.NEUTRAL, 1.5f, this.field_70146_Z.nextFloat() * 0.2f + 0.9f, false);
        if (this.field_70170_p.field_72995_K) {
            this.addAnimationTrigger("tektopia:villager_summon", 12, enchantSound);
            this.addAnimationTrigger("tektopia:villager_summon", 20, () -> {
                this.handParticleTick = 25;
            });
            this.addAnimationTrigger("tektopia:villager_summon", 90, enchantSound);
            this.addAnimationTrigger("tektopia:villager_summon", 130, enchantApplySound);
        }
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        Predicate<ItemStack> isLapis = p -> p.func_77973_b() == Items.field_151100_aR && p.func_77960_j() == EnumDyeColor.BLUE.func_176767_b();
        this.getDesireSet().addItemDesire(new ItemDesire("Lapis", isLapis, 1, 10, 20, null));
        craftSet.forEach(r -> this.getDesireSet().addRecipeDesire((Recipe)r));
        enchantSet.forEach(r -> this.getDesireSet().addRecipeDesire((Recipe)r));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAICraftItems(this, craftSet, "villager_craft", null, 80, VillageStructureType.LIBRARY, Blocks.field_150462_ai, p -> p.isWorkTime()));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIEnchant(this, enchantSet, "villager_summon", new ItemStack(Items.field_190931_a), 80, VillageStructureType.LIBRARY, Blocks.field_150381_bn, p -> p.isWorkTime(), 1));
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_187214_a(SPELL_BLOCK, (Object)Optional.absent());
        craftSet.forEach(r -> this.registerAIFilter(r.getAiFilter(), RECIPE_PARAMS.get(r.getAiFilter())));
        enchantSet.forEach(r -> this.registerAIFilter(r.getAiFilter(), RECIPE_PARAMS.get(r.getAiFilter())));
    }

    private static List<Recipe> buildEnchantSet() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        ArrayList<Object> ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Items.field_151040_l, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        ItemStack enchanted = new ItemStack(Items.field_151040_l, 1);
        enchanted.func_77966_a(Enchantments.field_185302_k, 1);
        Recipe recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_iron_sword", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 12), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151036_c, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack(Items.field_151036_c, 1);
        enchanted.func_77966_a(Enchantments.field_185305_q, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_iron_axe", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151035_b, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack(Items.field_151035_b, 1);
        enchanted.func_77966_a(Enchantments.field_185305_q, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_iron_pickaxe", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151030_Z, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151030_Z, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_iron_chestplate", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151028_Y, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151028_Y, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_iron_helmet", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151167_ab, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151167_ab, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_iron_boots", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151165_aa, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151165_aa, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_iron_leggings", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151027_R, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151027_R, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_leather_chestplate", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151024_Q, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151024_Q, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_leather_helmet", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151021_T, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151021_T, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_leather_boots", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151026_S, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151026_S, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_leather_leggings", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151048_u, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack(Items.field_151048_u, 1);
        enchanted.func_77966_a(Enchantments.field_185302_k, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_diamond_sword", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151056_x, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack(Items.field_151056_x, 1);
        enchanted.func_77966_a(Enchantments.field_185305_q, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_diamond_axe", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151046_w, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack(Items.field_151046_w, 1);
        enchanted.func_77966_a(Enchantments.field_185305_q, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_diamond_pickaxe", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151163_ad, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151163_ad, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_diamond_chestplate", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151161_ac, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151161_ac, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_diamond_helmet", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151175_af, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151175_af, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_diamond_boots", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack((Item)Items.field_151173_ae, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack((Item)Items.field_151173_ae, 1);
        enchanted.func_77966_a(Enchantments.field_180310_c, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_diamond_leggings", 1, enchanted, ingredients, 1, 1, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 3);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151122_aG, 1));
        ingredients.add(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()));
        enchanted = new ItemStack(Items.field_151134_bR, 1);
        enchanted.func_77966_a(Enchantments.field_185296_A, 1);
        recipe = new Recipe(ProfessionType.ENCHANTER, "enchant_book", 1, enchanted, ingredients, 2, 3, v -> v.getSkillLerp(ProfessionType.ENCHANTER, 4, 15), 10);
        recipes.add(recipe);
        return recipes;
    }

    private static List<Recipe> buildCraftSet() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        ArrayList<Object> ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Items.field_151120_aE, 3));
        Recipe recipe = new Recipe(ProfessionType.ENCHANTER, "craft_paper", 8, new ItemStack(Items.field_151121_aF, 3), ingredients, 2, 6, v -> 2, 128);
        recipes.add(recipe);
        ingredients = new ArrayList();
        ingredients.add(new ItemStack(Items.field_151121_aF, 3));
        ingredients.add(new ItemStack(Items.field_151116_aA, 1));
        recipe = new Recipe(ProfessionType.ENCHANTER, "craft_book", 3, new ItemStack(Items.field_151122_aG, 1), ingredients, 3, 5, v -> 2, 32);
        recipes.add(recipe);
        return recipes;
    }

    public boolean isSpellcasting() {
        BlockPos blockPos = this.getSpellBlock();
        return blockPos != null;
    }

    public BlockPos getSpellBlock() {
        return (BlockPos)((Optional)this.field_70180_af.func_187225_a(SPELL_BLOCK)).orNull();
    }

    public void setCasting(BlockPos castingPos) {
        if (castingPos == null) {
            this.field_70180_af.func_187227_b(SPELL_BLOCK, (Object)Optional.absent());
        } else {
            this.field_70180_af.func_187227_b(SPELL_BLOCK, (Object)Optional.of((Object)castingPos));
        }
    }

    public void func_70071_h_() {
        super.func_70071_h_();
        if (this.field_70170_p.field_72995_K) {
            int skill = this.getSkill(ProfessionType.ENCHANTER);
            if (this.isSpellcasting()) {
                this.clientSpellBlock = this.getSpellBlock();
                this.blockParticleTick = 10;
                if (this.handParticleTick > 0) {
                    double particles;
                    double d0 = 0.7;
                    double d1 = 0.7;
                    double d2 = 0.7;
                    float f = this.field_70761_aq * ((float)Math.PI / 180) + MathHelper.func_76134_b((float)((float)this.field_70173_aa * 0.6662f)) * 0.25f;
                    float f1 = MathHelper.func_76134_b((float)f);
                    float f2 = MathHelper.func_76126_a((float)f);
                    for (particles = 1.0; particles > 1.0; particles -= 1.0) {
                        this.field_70170_p.func_175688_a(EnumParticleTypes.SPELL_MOB, this.field_70165_t + (double)f1 * 0.6, this.field_70163_u + 2.0, this.field_70161_v + (double)f2 * 0.6, d0, d1, d2, new int[]{0, 1, 1});
                        this.field_70170_p.func_175688_a(EnumParticleTypes.SPELL_MOB, this.field_70165_t - (double)f1 * 0.6, this.field_70163_u + 2.0, this.field_70161_v - (double)f2 * 0.6, d0, d1, d2, new int[]{0, 1, 1});
                    }
                    if (this.func_70681_au().nextDouble() < particles) {
                        this.field_70170_p.func_175688_a(EnumParticleTypes.SPELL_MOB, this.field_70165_t + (double)f1 * 0.6, this.field_70163_u + 2.0, this.field_70161_v + (double)f2 * 0.6, d0, d1, d2, new int[]{0, 1, 1});
                        this.field_70170_p.func_175688_a(EnumParticleTypes.SPELL_MOB, this.field_70165_t - (double)f1 * 0.6, this.field_70163_u + 2.0, this.field_70161_v - (double)f2 * 0.6, d0, d1, d2, new int[]{0, 1, 1});
                    }
                    --this.handParticleTick;
                }
            }
            --this.blockParticleTick;
            if (this.blockParticleTick > 0) {
                this.field_70170_p.func_175688_a(EnumParticleTypes.ENCHANTMENT_TABLE, (double)this.clientSpellBlock.func_177958_n() + 0.5, (double)this.clientSpellBlock.func_177956_o() + 3.1, (double)this.clientSpellBlock.func_177952_p() + 0.5, -1.2 + (double)this.field_70170_p.field_73012_v.nextFloat() * 2.4, (double)(this.field_70146_Z.nextFloat() - 1.0f), -1.2 + (double)this.field_70170_p.field_73012_v.nextFloat() * 2.4, new int[0]);
            }
        }
    }

    static {
        enchantSet.forEach(r -> RECIPE_PARAMS.put(r.getAiFilter(), (DataParameter<Boolean>)EntityDataManager.func_187226_a(EntityEnchanter.class, (DataSerializer)DataSerializers.field_187198_h)));
        craftSet.forEach(r -> RECIPE_PARAMS.put(r.getAiFilter(), (DataParameter<Boolean>)EntityDataManager.func_187226_a(EntityEnchanter.class, (DataSerializer)DataSerializers.field_187198_h)));
        animHandler.addAnim("tektopia", "villager_summon", "enchanter_m", true);
        animHandler.addAnim("tektopia", "villager_craft", "enchanter_m", true);
        EntityVillagerTek.setupAnimations(animHandler, "enchanter_m");
    }
}

