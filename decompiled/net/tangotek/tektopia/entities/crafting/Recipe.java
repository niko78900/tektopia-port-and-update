/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package net.tangotek.tektopia.entities.crafting;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.storage.VillagerInventory;

public class Recipe {
    private final ProfessionType profession;
    private final int skillChance;
    private final ItemStack product;
    private final List<ItemStack> needs;
    private final int storageGoal;
    public final int idealCount;
    public final int limitCount;
    public final String aiFilter;
    public final Predicate<EntityVillagerTek> shouldCraft;
    private final Function<EntityVillagerTek, Integer> iterations;

    public Recipe(ProfessionType pt, String aiFilter, int skillChance, ItemStack itemProduct, List<ItemStack> needs, int idealCount, int limitCount, Function<EntityVillagerTek, Integer> animationIterations, int storageGoal) {
        this(pt, aiFilter, skillChance, itemProduct, needs, idealCount, limitCount, animationIterations, storageGoal, null);
    }

    public Recipe(ProfessionType pt, String aiFilter, int skillChance, ItemStack itemProduct, List<ItemStack> needs, int idealCount, int limitCount, Function<EntityVillagerTek, Integer> animationIterations, int storageGoal, Predicate<EntityVillagerTek> pred) {
        this.profession = pt;
        this.skillChance = skillChance;
        this.aiFilter = aiFilter;
        this.product = itemProduct;
        this.needs = needs;
        this.storageGoal = storageGoal;
        this.shouldCraft = pred;
        this.iterations = animationIterations;
        this.idealCount = idealCount;
        this.limitCount = limitCount;
    }

    public boolean hasItems(EntityVillagerTek villager) {
        for (ItemStack itemReq : this.needs) {
            int reqCount = villager.getInventory().getItemCount((Predicate<ItemStack>)(p -> p.func_77973_b() == itemReq.func_77973_b() && !p.func_77948_v()));
            if (reqCount >= itemReq.func_190916_E()) continue;
            return false;
        }
        return true;
    }

    public String getAiFilter() {
        return this.aiFilter;
    }

    public ItemStack craft(EntityVillagerTek villager) {
        boolean nonVillagerItems = false;
        List<ItemStack> consumedItems = new java.util.ArrayList<ItemStack>();
        for (ItemStack itemReq : this.needs) {
            List<ItemStack> items = villager.getInventory().removeItems((Predicate<ItemStack>)(p -> p.func_77973_b() == itemReq.func_77973_b() && !p.func_77948_v()), itemReq.func_190916_E());
            int total = VillagerInventory.countItems(items);
            if (total != itemReq.func_190916_E()) {
                for (ItemStack restored : consumedItems) {
                    villager.getInventory().func_174894_a(restored);
                }
                for (ItemStack restored : items) {
                    villager.getInventory().func_174894_a(restored);
                }
                villager.debugOut("Craft aborted: missing ingredients for " + this.product.func_77973_b().func_77658_a());
                return null;
            }
            consumedItems.addAll(items);
            nonVillagerItems |= items.stream().anyMatch(itemStack -> !ModItems.isTaggedItem(itemStack, ItemTagType.VILLAGER));
        }
        villager.tryAddSkill(this.profession, this.skillChance);
        villager.debugOut("has crafted: " + this.product.func_77973_b().func_77658_a());
        ItemStack result = this.product.func_77946_l();
        if (!nonVillagerItems) {
            ModItems.makeTaggedItem(result, ItemTagType.VILLAGER);
        }
        return result;
    }

    public ItemStack getProduct() {
        return this.product;
    }

    public Predicate<ItemStack> isNeed() {
        return p -> {
            for (ItemStack need : this.needs) {
                if (need.func_77973_b() != p.func_77973_b()) continue;
                return true;
            }
            return false;
        };
    }

    public List<ItemStack> getNeeds() {
        return this.needs;
    }

    public static boolean hasPersonalGoal(EntityVillagerTek villager, ItemStack goalStack) {
        Predicate<ItemStack> pred = p -> p.func_77973_b() == goalStack.func_77973_b();
        int count = villager.getInventory().getItemCount(pred);
        return count >= goalStack.func_190916_E();
    }

    public boolean shouldCraft(EntityVillagerTek villager) {
        if (!villager.hasVillage() || !villager.isAIFilterEnabled(this.aiFilter)) {
            return false;
        }
        int storageCount = villager.getVillage().getStorageCount(p -> p.func_77973_b() == this.getProduct().func_77973_b() && p.func_77948_v() == this.getProduct().func_77948_v());
        if (storageCount >= this.storageGoal) {
            return false;
        }
        return this.shouldCraft == null || this.shouldCraft.test(villager);
    }

    public int getAnimationIterations(EntityVillagerTek v) {
        return this.iterations.apply(v);
    }
}
