/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemStack
 */
package net.tangotek.tektopia.storage;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.storage.ItemDesire;

public class UpgradeEquipment
extends ItemDesire {
    private Function<ItemStack, Integer> upgradeFunction;
    private final EntityEquipmentSlot equipSlot;
    private int currentScore = 0;

    public UpgradeEquipment(String name, Function<ItemStack, Integer> itemFunction, EntityEquipmentSlot equipSlot, Predicate<EntityVillagerTek> should) {
        super(name, itemFunction, 0, 0, 0, should);
        this.equipSlot = equipSlot;
        this.upgradeFunction = p -> {
            int diff = (Integer)this.neededItemFunction.apply(p) - this.currentScore;
            return diff > 0 ? diff : -1;
        };
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
        if (this.selfDirty) {
            int oldScore = this.currentScore;
            villager.equipBestGear(this.equipSlot, this.neededItemFunction);
            this.currentScore = (Integer)this.neededItemFunction.apply(villager.func_184582_a(this.equipSlot));
            if (this.currentScore != oldScore) {
                this.storageDirty = true;
            }
            this.selfDirty = false;
        }
    }

    @Override
    public boolean shouldPickUp(EntityVillagerTek villager) {
        if ((this.shouldNeed == null || this.shouldNeed.test(villager)) && villager.hasVillage() && villager.isWorkTime()) {
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
        return Math.min(1, item.func_190916_E());
    }

    @Override
    public int getDeliverToStorage(EntityVillagerTek villager, ItemStack itemStack) {
        this.update(villager);
        return itemStack.func_190916_E();
    }
}

