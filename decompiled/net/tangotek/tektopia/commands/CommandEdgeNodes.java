/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 */
package net.tangotek.tektopia.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.commands.CommandVillageBase;

class CommandEdgeNodes
extends CommandVillageBase {
    public CommandEdgeNodes() {
        super("edges");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean enable = false;
        if (args.length != 0) {
            throw new WrongUsageException("commands.village.edge.usage", new Object[0]);
        }
        EntityPlayerMP entityPlayer = CommandEdgeNodes.func_71521_c((ICommandSender)sender);
        Village village = VillageManager.get(entityPlayer.field_70170_p).getNearestVillage(entityPlayer.func_180425_c(), 200);
        if (village != null) {
            village.getPathingGraph().debugEdgeNodes(entityPlayer.field_70170_p);
        }
    }
}

