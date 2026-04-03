/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 */
package net.tangotek.tektopia.storage;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryScanner {
    private final IInventory inv;
    private int slot = 0;
    private final byte[] slotHashes;

    public InventoryScanner(IInventory i) {
        this.inv = i;
        this.slotHashes = new byte[i.func_70302_i_()];
    }

    public int tickSlot() {
        byte hash;
        ++this.slot;
        if (this.slot >= this.inv.func_70302_i_()) {
            this.slot = 0;
        }
        if ((hash = this.getSlotHash(this.slot)) != this.slotHashes[this.slot]) {
            this.slotHashes[this.slot] = hash;
            return this.slot;
        }
        return -1;
    }

    public void updateSlotSilent(int slot) {
        this.slotHashes[slot] = this.getSlotHash(slot);
    }

    public ItemStack getChangedItem() {
        return this.inv.func_70301_a(this.slot);
    }

    private byte getSlotHash(int slot) {
        return (byte)this.inv.func_70301_a(slot).func_190916_E();
    }
}

