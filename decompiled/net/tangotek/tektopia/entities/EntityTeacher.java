/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
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
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAICraftItems;
import net.tangotek.tektopia.entities.ai.EntityAISchoolTeach;
import net.tangotek.tektopia.entities.crafting.Recipe;
import net.tangotek.tektopia.storage.ItemDesire;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityTeacher
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityTeacher.class);
    private static final DataParameter<Boolean> TEACH_SCHOOL = EntityDataManager.func_187226_a(EntityTeacher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static List<Recipe> craftSet = EntityTeacher.buildCraftSet();
    private static final Map<String, DataParameter<Boolean>> RECIPE_PARAMS;

    public EntityTeacher(World worldIn) {
        super(worldIn, ProfessionType.TEACHER, VillagerRole.VILLAGER.value);
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        craftSet.forEach(r -> this.registerAIFilter(r.getAiFilter(), RECIPE_PARAMS.get(r.getAiFilter())));
        this.registerAIFilter("teach_school", TEACH_SCHOOL);
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        craftSet = this.buildCraftSet();
        this.getDesireSet().addItemDesire(new ItemDesire("Book", p -> p.func_77973_b() == Items.field_151122_aG && !p.func_77948_v(), 0, 6, 9, p -> !EntityTeacher.isSchoolTime(this.field_70170_p) && p.isAIFilterEnabled("teach_school")));
        craftSet.forEach(r -> this.getDesireSet().addRecipeDesire((Recipe)r));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAISchoolTeach(this));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAICraftItems(this, craftSet, "villager_craft", null, 60, VillageStructureType.SCHOOL, Blocks.field_150462_ai, p -> {
            if (!p.isWorkTime()) return false;
            if (EntityTeacher.isSchoolTime(this.field_70170_p)) return false;
            return true;
        }));
    }

    @Override
    protected void randomizeGoals() {
        super.randomizeGoals();
        this.wantsLearning = this.func_70681_au().nextInt(6) + 3;
    }

    @Override
    public boolean isLearningTime() {
        if (this.wantsLearning > 0 && !EntityTeacher.isSchoolTime(this.field_70170_p) && this.isWorkTime()) {
            return this.isAIFilterEnabled("read_book");
        }
        return false;
    }

    private static List<Recipe> buildCraftSet() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Items.field_151121_aF, 2));
        ingredients.add(new ItemStack(Items.field_151008_G, 1));
        Recipe recipe = new Recipe(ProfessionType.TEACHER, "craft_name_tag", 10, new ItemStack(Items.field_151057_cb, 1), ingredients, 3, 5, v -> v.getSkillLerp(ProfessionType.TEACHER, 4, 1), 3);
        recipes.add(recipe);
        return recipes;
    }

    @Override
    protected boolean canVillagerPickupItem(ItemStack itemIn) {
        return itemIn.func_77973_b() == Items.field_151122_aG && !itemIn.func_77948_v();
    }

    public static boolean isSchoolTime(World world) {
        return Village.isTimeOfDay(world, 1000L, 8500L);
    }

    static {
        animHandler.addAnim("tektopia", "villager_teach", "teacher_m", false);
        animHandler.addAnim("tektopia", "villager_craft", "teacher_m", false);
        EntityVillagerTek.setupAnimations(animHandler, "teacher_m");
        RECIPE_PARAMS = new HashMap<String, DataParameter<Boolean>>();
        craftSet.forEach(r -> RECIPE_PARAMS.put(r.getAiFilter(), (DataParameter<Boolean>)EntityDataManager.func_187226_a(EntityTeacher.class, (DataSerializer)DataSerializers.field_187198_h)));
    }
}

