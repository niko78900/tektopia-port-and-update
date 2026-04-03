/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class VillageStructureFactory {
    public static VillageStructure create(VillageStructureType t, World w, Village v, EntityItemFrame i) {
        VillageStructure struct = t.create(w, v, i);
        struct.setup();
        return struct;
    }

    public static VillageStructureType getByItem(ItemStack i) {
        for (VillageStructureType t : VillageStructureType.values()) {
            if (!t.isItemEqual(i)) continue;
            return t;
        }
        return null;
    }
}

