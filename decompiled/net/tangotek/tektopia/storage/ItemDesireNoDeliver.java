/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package net.tangotek.tektopia.storage;

import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.storage.ItemDesire;

public class ItemDesireNoDeliver
extends ItemDesire {
    private final Predicate<ItemStack> pred;

    public ItemDesireNoDeliver(String name, Predicate<ItemStack> pred, int required, int ideal, int limit, Predicate<EntityVillagerTek> shouldNeed) {
        super(name, pred, required, ideal, limit, shouldNeed);
        this.pred = pred;
    }

    @Override
    public int getDeliverToStorage(EntityVillagerTek villager, ItemStack itemStack) {
        if ((Integer)this.neededItemFunction.apply(itemStack) < 0) {
            return itemStack.func_190916_E();
        }
        return 0;
    }

    @Override
    protected int getItemsHave(EntityVillagerTek villager) {
        int result = super.getItemsHave(villager);
        if (result > this.limitCount) {
            villager.getInventory().deleteOverstock(this.pred, this.limitCount);
            result = this.limitCount;
        }
        return result;
    }
}

