/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemFood
 *  net.minecraft.potion.PotionEffect
 */
package net.tangotek.tektopia.items;

import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import net.tangotek.tektopia.TekVillager;

public class ItemBeer
extends ItemFood {
    private String name;

    public ItemBeer(String name) {
        super(1, 4.0f, false);
        this.setRegistryName(name);
        this.func_77655_b(name);
        this.func_185070_a(new PotionEffect(MobEffects.field_76431_k, 160, 0), 0.5f);
        this.name = name;
    }

    public void registerItemModel() {
        TekVillager.proxy.registerItemRenderer((Item)this, 0, this.name);
    }
}

