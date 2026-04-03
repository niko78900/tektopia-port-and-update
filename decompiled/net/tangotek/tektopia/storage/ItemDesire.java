/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntityChest
 */
package net.tangotek.tektopia.storage;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class ItemDesire {
    protected boolean selfDirty = true;
    protected boolean storageDirty = true;
    protected final int idealCount;
    protected final int requiredCount;
    protected final int limitCount;
    protected int currentlyHave;
    protected Function<ItemStack, Integer> neededItemFunction;
    protected final Predicate<EntityVillagerTek> shouldNeed;
    private String debugName;
    protected TileEntityChest pickUpChest;

    public ItemDesire(Block block, int required, int ideal, int limit, Predicate<EntityVillagerTek> shouldNeed) {
        this(Item.func_150898_a((Block)block), required, ideal, limit, shouldNeed);
    }

    public ItemDesire(Item item, int required, int ideal, int limit, Predicate<EntityVillagerTek> shouldNeed) {
        this(item.func_77658_a(), (ItemStack p) -> p.func_77973_b() == item ? 1 : -1, required, ideal, limit, shouldNeed);
    }

    public ItemDesire(String name, Predicate<ItemStack> pred, int required, int ideal, int limit, Predicate<EntityVillagerTek> shouldNeed) {
        this(name, (ItemStack p) -> pred.test((ItemStack)p) ? 1 : -1, required, ideal, limit, shouldNeed);
    }

    public ItemDesire(String name, Function<ItemStack, Integer> itemFunction, int required, int ideal, int limit, Predicate<EntityVillagerTek> should) {
        this.neededItemFunction = itemFunction;
        this.idealCount = Math.max(ideal, required);
        this.limitCount = Math.max(this.idealCount, limit);
        this.requiredCount = required;
        this.shouldNeed = should;
        this.currentlyHave = 0;
        this.debugName = name;
    }

    public String name() {
        return this.debugName;
    }

    public void forceUpdate() {
        this.storageDirty = true;
        this.selfDirty = true;
    }

    public void onInventoryUpdated(EntityVillagerTek villager, ItemStack updatedItem) {
        if (this.neededItemFunction.apply(updatedItem) > 0) {
            this.selfDirty = true;
        }
    }

    public void onStorageUpdated(EntityVillagerTek villager, ItemStack updatedItem) {
        if (this.neededItemFunction.apply(updatedItem) > 0) {
            this.storageDirty = true;
        }
    }

    protected int getItemsHave(EntityVillagerTek villager) {
        return villager.getInventory().getItemCount(this.neededItemFunction);
    }

    protected void updateStorage(EntityVillagerTek villager) {
        if (this.storageDirty && villager.hasVillage()) {
            this.pickUpChest = this.currentlyHave < this.idealCount ? villager.getVillage().getStorageChestWithItem(this.neededItemFunction) : null;
            this.storageDirty = false;
        }
    }

    protected void updateSelf(EntityVillagerTek villager) {
        if (this.selfDirty) {
            int oldHave = this.currentlyHave;
            this.currentlyHave = this.getItemsHave(villager);
            if (this.currentlyHave < oldHave) {
                this.storageDirty = true;
            }
            this.selfDirty = false;
        }
    }

    protected void update(EntityVillagerTek villager) {
        this.updateSelf(villager);
        this.updateStorage(villager);
    }

    public TileEntityChest getPickUpChest(EntityVillagerTek villager) {
        this.update(villager);
        if (this.pickUpChest != null && this.pickUpChest.func_145837_r()) {
            this.pickUpChest = null;
        }
        return this.pickUpChest;
    }

    public boolean shouldPickUp(EntityVillagerTek villager) {
        if ((this.shouldNeed == null || this.shouldNeed.test(villager)) && villager.hasVillage() && villager.isWorkTime()) {
            this.update(villager);
            if (this.currentlyHave < this.idealCount && this.pickUpChest != null) {
                if (villager.isStoragePriority()) {
                    return true;
                }
                if (this.currentlyHave < this.requiredCount) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Function<ItemStack, Integer> getStoragePickUpFunction() {
        return this.neededItemFunction;
    }

    protected int getQuantityToTake(EntityVillagerTek villager, ItemStack item) {
        return Math.min(this.idealCount - this.currentlyHave, item.func_190916_E());
    }

    public ItemStack pickUpItems(EntityVillagerTek villager) {
        if (this.shouldPickUp(villager)) {
            TileEntityChest chest = this.getPickUpChest(villager);
            ItemStack bestItem = null;
            int bestSlot = 0;
            int bestScore = 0;
            for (int d = 0; d < chest.func_70302_i_(); ++d) {
                int thisScore;
                ItemStack chestStack = chest.func_70301_a(d);
                if (chestStack.func_190926_b() || (thisScore = this.getStoragePickUpFunction().apply(chestStack).intValue()) <= bestScore) continue;
                bestItem = chestStack;
                bestScore = thisScore;
                bestSlot = d;
            }
            if (bestItem != null) {
                villager.throttledSadness(-2);
                int quantityToTake = this.getQuantityToTake(villager, bestItem);
                villager.equipActionItem(bestItem);
                ItemStack newStack = bestItem.func_77979_a(quantityToTake);
                if (newStack.func_190926_b()) {
                    villager.getDesireSet().forceUpdate();
                } else {
                    villager.getVillage().onStorageChange(chest, bestSlot, newStack);
                }
                return newStack;
            }
        }
        return ItemStack.field_190927_a;
    }

    public int getDeliverToStorage(EntityVillagerTek villager, ItemStack itemStack) {
        int thisLimit;
        this.update(villager);
        if (this.neededItemFunction.apply(itemStack) < 0) {
            return itemStack.func_190916_E();
        }
        int n = thisLimit = villager.isStoragePriority() ? this.idealCount : this.limitCount;
        if (this.currentlyHave - thisLimit > 0) {
            return Math.min(this.currentlyHave - this.idealCount, itemStack.func_190916_E());
        }
        return 0;
    }
}

