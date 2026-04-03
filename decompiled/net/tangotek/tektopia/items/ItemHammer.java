/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemSword
 */
package net.tangotek.tektopia.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.tangotek.tektopia.TekVillager;

public class ItemHammer
extends ItemSword {
    private String name;

    public ItemHammer(Item.ToolMaterial material, String name) {
        super(material);
        this.setRegistryName(name);
        this.func_77655_b(name);
        this.name = name;
    }

    public float func_150931_i() {
        return 1.0f;
    }

    public void registerItemModel() {
        TekVillager.proxy.registerItemRenderer((Item)this, 0, this.name);
    }
}

