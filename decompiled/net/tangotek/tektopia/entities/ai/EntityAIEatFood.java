/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package net.tangotek.tektopia.entities.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class EntityAIEatFood
extends EntityAIBase {
    private EntityVillagerTek villager;
    private ItemStack foodItem;
    private int eatTime = 0;
    public static final Map<Item, VillagerFood> villagerFood = new HashMap<Item, VillagerFood>();

    private static void registerFood(Item item, int hunger, int happy) {
        EntityAIEatFood.registerFood(item, hunger, happy, null);
    }

    private static void registerFood(Item item, int hunger, int happy, BiConsumer<EntityVillagerTek, ItemStack> postEat) {
        VillagerFood food = new VillagerFood(item, hunger, happy, postEat);
        villagerFood.put(food.item, food);
    }

    public EntityAIEatFood(EntityVillagerTek v) {
        this.villager = v;
        this.func_75248_a(1);
    }

    public boolean func_75252_g() {
        return false;
    }

    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.isHungry() && !this.villager.isSleeping()) {
            this.foodItem = this.villager.getInventory().getItem(p -> EntityAIEatFood.getFoodScore(p.func_77973_b(), this.villager));
            if (!this.foodItem.func_190926_b()) {
                return true;
            }
            this.villager.setThought(EntityVillagerTek.VillagerThought.HUNGRY);
        }
        return false;
    }

    public static int getFoodScore(Item item, EntityVillagerTek v) {
        VillagerFood food = villagerFood.get(item);
        if (food != null) {
            return EntityAIEatFood.getFoodScore(food, v);
        }
        return -1;
    }

    public static int getFoodScore(VillagerFood food, EntityVillagerTek v) {
        if (v != null) {
            int happy = food.getHappy(v);
            int hunger = food.getHunger(v);
            if (v.getHunger() + hunger > v.getMaxHunger()) {
                hunger = 1;
            }
            if (v.getHappy() + happy > v.getMaxHappy()) {
                happy = 0;
            }
            int score = hunger;
            int happyPotential = happy * 5;
            float happyFactor = 1.0f;
            if (happyPotential > 0) {
                happyFactor = (float)(v.getMaxHappy() - v.getHappy()) / (float)v.getMaxHappy();
            }
            return Math.max(score += (int)((float)happyPotential * happyFactor), 1);
        }
        return food.happy + food.hunger;
    }

    public void func_75249_e() {
        this.startEat();
        super.func_75249_e();
    }

    public boolean func_75253_b() {
        return this.eatTime >= 0;
    }

    public void func_75246_d() {
        VillagerFood food;
        --this.eatTime;
        if (this.eatTime == 0 && !this.villager.getInventory().removeItems(p -> ItemStack.func_77989_b((ItemStack)p, (ItemStack)this.foodItem), 1).isEmpty() && (food = villagerFood.get(this.foodItem.func_77973_b())) != null) {
            food.eat(this.villager, this.foodItem);
        }
        super.func_75246_d();
    }

    private void startEat() {
        this.eatTime = 80;
        this.villager.func_70661_as().func_75499_g();
        this.villager.equipActionItem(this.foodItem);
        this.villager.playServerAnimation("villager_eat");
    }

    private void stopEat() {
        this.villager.unequipActionItem(this.foodItem);
        this.villager.stopServerAnimation("villager_eat");
    }

    public void func_75251_c() {
        super.func_75251_c();
        this.stopEat();
        this.foodItem = null;
        this.eatTime = 0;
    }

    static {
        BiConsumer<EntityVillagerTek, ItemStack> returnBowl = (v, i) -> {
            ItemStack bowl = new ItemStack(Items.field_151054_z);
            if (ModItems.isTaggedItem(i, ItemTagType.VILLAGER)) {
                ModItems.makeTaggedItem(bowl, ItemTagType.VILLAGER);
            }
            v.getInventory().func_174894_a(bowl);
        };
        EntityAIEatFood.registerFood(Items.field_151034_e, 12, -1);
        EntityAIEatFood.registerFood(Items.field_151168_bH, 35, 1);
        EntityAIEatFood.registerFood(Items.field_185164_cV, 7, -1);
        EntityAIEatFood.registerFood(Items.field_185165_cW, 50, 6, returnBowl);
        EntityAIEatFood.registerFood(Items.field_151025_P, 55, 4);
        EntityAIEatFood.registerFood(Items.field_151105_aU, 7, 25);
        EntityAIEatFood.registerFood(Items.field_151172_bF, 12, -1);
        EntityAIEatFood.registerFood(Items.field_151083_be, 70, 14);
        EntityAIEatFood.registerFood(Items.field_151077_bg, 60, 6);
        EntityAIEatFood.registerFood(Items.field_179557_bn, 66, 4);
        EntityAIEatFood.registerFood(Items.field_151157_am, 70, 14);
        EntityAIEatFood.registerFood(Items.field_151106_aX, 5, 16);
        EntityAIEatFood.registerFood(Items.field_151150_bK, 70, 20);
        EntityAIEatFood.registerFood(Items.field_151127_ba, 6, 3);
        EntityAIEatFood.registerFood(Items.field_151009_A, 50, 4, returnBowl);
        EntityAIEatFood.registerFood(Items.field_151174_bG, 7, -1);
        EntityAIEatFood.registerFood(Items.field_151158_bO, 35, 18);
    }

    public static class VillagerFood {
        private final int happy;
        private final int hunger;
        private final Item item;
        private final BiConsumer<EntityVillagerTek, ItemStack> postEat;

        public VillagerFood(Item item, int hunger, int happy) {
            this(item, hunger, happy, null);
        }

        public VillagerFood(Item item, int hunger, int happy, BiConsumer<EntityVillagerTek, ItemStack> post) {
            this.item = item;
            this.hunger = hunger;
            this.happy = happy;
            this.postEat = post;
        }

        public void eat(EntityVillagerTek v, ItemStack foodItem) {
            boolean isVFood = ModItems.isTaggedItem(foodItem, ItemTagType.VILLAGER);
            int hunger = this.getHunger(v);
            if (!isVFood) {
                hunger /= 2;
            }
            v.modifyHunger(hunger);
            if (isVFood) {
                int happy = this.getHappy(v);
                v.modifyHappy(happy);
            }
            if (this.postEat != null) {
                this.postEat.accept(v, foodItem);
            }
            v.debugOut("Eating Food " + foodItem.func_77973_b().func_77658_a());
            v.addRecentEat(this.item);
        }

        public int getHappy(EntityVillagerTek villager) {
            int recentEatModifier = villager.getRecentEatModifier(this.item);
            return Math.max(this.happy + recentEatModifier, -3);
        }

        public int getHunger(EntityVillagerTek villager) {
            return this.hunger;
        }

        public Item getItem() {
            return this.item;
        }
    }
}

