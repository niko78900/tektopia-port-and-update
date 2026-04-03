/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package net.tangotek.tektopia.storage;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.storage.ItemDesire;

public class UpgradeItemDesire
extends ItemDesire {
    int scoreCutoff;
    int currentlyHaveIdeal = 0;
    Function<ItemStack, Integer> upgradeFunction = p -> {
        int diff = (Integer)this.neededItemFunction.apply(p) - this.scoreCutoff;
        return diff > 0 ? diff : -1;
    };

    public UpgradeItemDesire(String name, Function<ItemStack, Integer> itemFunction, int required, int ideal, int limit, Predicate<EntityVillagerTek> should) {
        super(name, itemFunction, required, ideal, limit, should);
    }

    @Override
    public void onStorageUpdated(EntityVillagerTek villager, ItemStack updatedItem) {
        if (this.upgradeFunction.apply(updatedItem) > 0) {
            this.storageDirty = true;
        }
    }

    @Override
    protected void updateStorage(EntityVillagerTek villager) {
        if (this.storageDirty && villager.hasVillage()) {
            this.pickUpChest = villager.getVillage().getStorageChestWithItem(this.upgradeFunction);
            this.storageDirty = false;
        }
    }

    @Override
    protected void updateSelf(EntityVillagerTek villager) {
        if (this.selfDirty && villager.hasVillage()) {
            List<ItemStack> itemList = villager.getInventory().getItems(this.neededItemFunction, 0);
            this.currentlyHave = 0;
            this.currentlyHaveIdeal = 0;
            int cutOff = 0;
            for (ItemStack item : itemList) {
                this.currentlyHave += item.func_190916_E();
                if (this.currentlyHave < this.idealCount || cutOff > 0) continue;
                cutOff = (Integer)this.neededItemFunction.apply(item);
                this.currentlyHaveIdeal = this.currentlyHave;
            }
            if (cutOff != this.scoreCutoff) {
                this.scoreCutoff = cutOff;
                this.storageDirty = true;
            }
            this.selfDirty = false;
        }
    }

    @Override
    public boolean shouldPickUp(EntityVillagerTek villager) {
        if ((this.shouldNeed == null || this.shouldNeed.test(villager)) && villager.hasVillage()) {
            this.update(villager);
            if (this.pickUpChest != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Function<ItemStack, Integer> getStoragePickUpFunction() {
        return this.upgradeFunction;
    }

    @Override
    protected int getQuantityToTake(EntityVillagerTek villager, ItemStack item) {
        int thisScore = (Integer)this.neededItemFunction.apply(item);
        if (thisScore < 0) {
            return item.func_190916_E();
        }
        Function<ItemStack, Integer> betterThan = p -> {
            int diff = (Integer)this.neededItemFunction.apply(p) - thisScore;
            return diff >= 0 ? 1 : -1;
        };
        int itemsToTake = this.idealCount - villager.getInventory().getItemCount(betterThan);
        return Math.min(itemsToTake, item.func_190916_E());
    }

    @Override
    public int getDeliverToStorage(EntityVillagerTek villager, ItemStack itemStack) {
        int thisLimit;
        this.update(villager);
        int thisScore = (Integer)this.neededItemFunction.apply(itemStack);
        if (thisScore < 0) {
            return itemStack.func_190916_E();
        }
        int n = thisLimit = villager.isStoragePriority() ? this.idealCount : this.limitCount;
        if (this.currentlyHave - thisLimit > 0) {
            if (thisScore < this.scoreCutoff) {
                return Math.min(this.currentlyHave - this.idealCount, itemStack.func_190916_E());
            }
            if (thisScore == this.scoreCutoff) {
                return Math.min(this.currentlyHaveIdeal - this.idealCount, itemStack.func_190916_E());
            }
        }
        return 0;
    }
}

