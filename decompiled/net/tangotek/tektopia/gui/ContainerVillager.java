/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IContainerListener
 *  net.minecraft.inventory.IInventory
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.gui.ReadOnlySlot;
import net.tangotek.tektopia.storage.VillagerInventory;

public class ContainerVillager
extends Container {
    private final IInventory villagerInventory;

    public ContainerVillager(VillagerInventory villagerInventory) {
        this.villagerInventory = villagerInventory;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.func_75146_a(new ReadOnlySlot((IInventory)villagerInventory, i * 9 + j, 9 + j * 18, 97 + i * 18));
            }
        }
    }

    public void func_75132_a(IContainerListener listener) {
        super.func_75132_a(listener);
        listener.func_175173_a((Container)this, this.villagerInventory);
    }

    @SideOnly(value=Side.CLIENT)
    public void func_75137_b(int id, int data) {
        this.villagerInventory.func_174885_b(id, data);
    }

    public boolean func_75145_c(EntityPlayer playerIn) {
        return true;
    }
}

