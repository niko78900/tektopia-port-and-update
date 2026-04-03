/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.items;

import java.util.function.BiFunction;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class ItemProfessionToken
extends Item {
    private final String name;
    private final int cost;
    private final ProfessionType professionType;
    private BiFunction<World, EntityVillagerTek, EntityVillagerTek> villagerFunc;

    public ItemProfessionToken(String name, ProfessionType pt, BiFunction<World, EntityVillagerTek, EntityVillagerTek> biFunction, int emeraldCost) {
        this.func_77637_a(CreativeTabs.field_78026_f);
        this.setRegistryName(name);
        this.func_77625_d(1);
        this.func_77655_b(name);
        this.name = name;
        this.professionType = pt;
        this.villagerFunc = biFunction;
        this.cost = emeraldCost;
    }

    public int getCost(Village v) {
        float mult = Math.min((float)(v.getTownData().getProfessionSales() / 5) * 0.2f, 10.0f);
        return (int)((float)this.cost * (1.0f + mult));
    }

    public ProfessionType getProfessionType() {
        return this.professionType;
    }

    public EntityVillagerTek createVillager(World world, EntityVillagerTek clickedEntity) {
        return this.villagerFunc.apply(world, clickedEntity);
    }

    public void registerItemModel() {
        TekVillager.proxy.registerItemRenderer(this, 0, this.name);
    }
}

