/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntityChest
 */
package net.tangotek.tektopia.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.crafting.Recipe;
import net.tangotek.tektopia.storage.ItemDesire;
import net.tangotek.tektopia.storage.VillagerInventory;

public class ItemDesireSet {
    protected List<ItemDesire> itemDesires = new ArrayList<ItemDesire>();
    protected boolean deliveryDirty = true;
    protected int deliveryId = 0;
    protected byte deliverySlot = (byte)-1;
    protected short deliveryCount = 0;
    protected int totalDeliverySize = 0;

    public void clear() {
        this.itemDesires.clear();
    }

    public void addItemDesire(ItemDesire desire) {
        this.itemDesires.add(desire);
    }

    public void addRecipeDesire(Recipe r) {
        for (ItemStack need : r.getNeeds()) {
            Predicate<ItemStack> pred = need.func_77960_j() == 99 ? p -> p.func_77973_b() == need.func_77973_b() && !p.func_77948_v() : p -> ItemStack.func_179545_c((ItemStack)p, (ItemStack)need) && !p.func_77948_v();
            Predicate<EntityVillagerTek> shouldNeed = p -> p.isAIFilterEnabled(r.getAiFilter());
            if (r.shouldCraft != null) {
                shouldNeed = shouldNeed.and(r.shouldCraft);
            }
            this.addItemDesire(new ItemDesire(need.func_77973_b().func_77658_a(), pred, need.func_190916_E(), need.func_190916_E() * r.idealCount, need.func_190916_E() * r.limitCount, shouldNeed));
        }
    }

    public void forceUpdate() {
        this.itemDesires.forEach(d -> d.forceUpdate());
        this.deliveryDirty = true;
    }

    public void onStorageUpdated(EntityVillagerTek villager, ItemStack storageItem) {
        this.itemDesires.forEach(d -> d.onStorageUpdated(villager, storageItem));
    }

    public void onInventoryUpdated(EntityVillagerTek villager, ItemStack updatedItem) {
        this.itemDesires.forEach(d -> d.onInventoryUpdated(villager, updatedItem));
        this.deliveryDirty = true;
    }

    public ItemDesire getNeededDesire(EntityVillagerTek villager) {
        for (ItemDesire d : this.itemDesires) {
            if (!d.shouldPickUp(villager)) continue;
            return d;
        }
        return null;
    }

    private void updateDeliveryList(EntityVillagerTek villager) {
        if (this.deliveryDirty && villager.hasVillage()) {
            this.deliverySlot = (byte)-1;
            this.deliveryCount = 0;
            this.totalDeliverySize = 0;
            int bestValue = 0;
            for (int i = villager.getInventory().func_70302_i_() - 1; i >= 0; --i) {
                ItemStack itemStack = villager.getInventory().func_70301_a(i);
                if (itemStack.func_190926_b()) continue;
                int minDeliver = Integer.MAX_VALUE;
                for (ItemDesire desire : this.itemDesires) {
                    int toDeliver = desire.getDeliverToStorage(villager, itemStack);
                    if (toDeliver <= 0) {
                        minDeliver = Integer.MAX_VALUE;
                        break;
                    }
                    if (toDeliver <= 0 || toDeliver >= minDeliver) continue;
                    minDeliver = toDeliver;
                }
                if (minDeliver >= Integer.MAX_VALUE) continue;
                int value = VillageManager.getItemValue(itemStack.func_77973_b()) * minDeliver;
                if (value > bestValue) {
                    bestValue = value;
                    this.deliveryCount = (short)minDeliver;
                    this.deliverySlot = (byte)i;
                }
                this.totalDeliverySize += value;
            }
            this.deliveryDirty = false;
            ++this.deliveryId;
        }
    }

    public int getDeliveryId(EntityVillagerTek villager, int requiredDeliverSize) {
        this.updateDeliveryList(villager);
        if (this.totalDeliverySize >= requiredDeliverSize) {
            return this.deliveryId;
        }
        return 0;
    }

    public boolean isDeliveryMatch(int id) {
        return this.deliveryId == id;
    }

    public ItemStack getDeliveryItemCopy(EntityVillagerTek villager) {
        this.updateDeliveryList(villager);
        if (this.deliverySlot >= 0) {
            ItemStack deliverItem = villager.getInventory().func_70301_a(this.deliverySlot).func_77946_l();
            deliverItem.func_190920_e((int)this.deliveryCount);
            return deliverItem;
        }
        return ItemStack.field_190927_a;
    }

    public boolean deliverItems(EntityVillagerTek villager, TileEntityChest destInv, int deliveryCheckId) {
        if (this.deliveryId != deliveryCheckId) {
            villager.debugOut("Delivery Id mismatch");
            this.deliveryDirty = true;
            return false;
        }
        if (this.deliverySlot < 0) {
            villager.debugOut("Delivery FAILED. No active delivery.");
            this.deliveryDirty = true;
            return false;
        }
        ItemStack removedStack = villager.getInventory().func_70298_a(this.deliverySlot, this.deliveryCount);
        if (removedStack == ItemStack.field_190927_a) {
            villager.debugOut("Delivery FAILED. Delivery item not found in villager inventory");
            this.deliveryDirty = true;
            return false;
        }
        if (!this.deliverOneItem(removedStack, destInv, villager)) {
            villager.debugOut("Delivery FAILED. Returning to villager inventory " + removedStack);
            villager.getInventory().func_174894_a(removedStack);
            this.deliveryDirty = true;
            return false;
        }
        return true;
    }

    private boolean deliverOneItem(ItemStack sourceStack, TileEntityChest destChest, EntityVillagerTek villager) {
        int emptySlot = -1;
        for (int d = 0; d < destChest.func_70302_i_(); ++d) {
            int k;
            ItemStack destStack = destChest.func_70301_a(d);
            if (destStack.func_190926_b() && emptySlot < 0) {
                emptySlot = d;
                continue;
            }
            if (!VillagerInventory.areItemsStackable(destStack, sourceStack) || (k = Math.min(sourceStack.func_190916_E(), destStack.func_77976_d() - destStack.func_190916_E())) <= 0) continue;
            destStack.func_190917_f(k);
            if (villager.hasVillage()) {
                villager.getVillage().onStorageChange(destChest, d, destStack);
            }
            villager.onInventoryUpdated(destStack);
            sourceStack.func_190918_g(k);
            if (!sourceStack.func_190926_b()) continue;
            return true;
        }
        if (emptySlot >= 0) {
            destChest.func_70299_a(emptySlot, sourceStack);
            if (villager.hasVillage()) {
                villager.getVillage().onStorageChange(destChest, emptySlot, sourceStack);
            }
            return true;
        }
        return false;
    }
}

