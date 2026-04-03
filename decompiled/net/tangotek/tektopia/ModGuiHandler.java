/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.IGuiHandler
 */
package net.tangotek.tektopia;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.gui.ContainerVillager;
import net.tangotek.tektopia.gui.GuiVillager;

public class ModGuiHandler
implements IGuiHandler {
    public static final int VILLAGER_INFO = 0;

    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0: {
                Entity ent = world.func_73045_a(x);
                if (!(ent instanceof EntityVillagerTek)) break;
                EntityVillagerTek villager = (EntityVillagerTek)ent;
                return new ContainerVillager(villager.getInventory());
            }
        }
        return null;
    }

    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0: {
                Entity ent = world.func_73045_a(x);
                if (!(ent instanceof EntityVillagerTek)) break;
                EntityVillagerTek villager = (EntityVillagerTek)ent;
                return new GuiVillager(villager);
            }
        }
        return null;
    }
}

