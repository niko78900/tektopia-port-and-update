/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package net.tangotek.tektopia.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.commands.CommandVillageBase;
import net.tangotek.tektopia.network.PacketPathingNode;

class CommandGraph
extends CommandVillageBase {
    public CommandGraph() {
        super("graph");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            throw new WrongUsageException("commands.village.graph.usage", new Object[0]);
        }
        EntityPlayerMP entityPlayer = CommandGraph.func_71521_c((ICommandSender)sender);
        VillageManager vm = VillageManager.get(entityPlayer.field_70170_p);
        Village village = vm.getVillageAt(entityPlayer.func_180425_c());
        if (village != null && entityPlayer instanceof EntityPlayerMP) {
            int mode = 1;
            if (args.length == 1) {
                mode = Integer.valueOf(args[0]);
            }
            EntityPlayerMP playerMP = entityPlayer;
            if (mode == 1) {
                village.getPathingGraph().addListener(playerMP);
            } else if (mode == 0) {
                village.getPathingGraph().removeListener(playerMP);
                TekVillager.NETWORK.sendTo((IMessage)new PacketPathingNode(null), playerMP);
            }
        }
    }
}

