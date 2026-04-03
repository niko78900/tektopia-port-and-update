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

class CommandRaid
extends CommandVillageBase {
    public CommandRaid() {
        super("raid");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            throw new WrongUsageException("commands.village.raid.usage", new Object[0]);
        }
        int raidLevel = 0;
        if (args.length == 1) {
            raidLevel = Integer.parseInt(args[0]);
        }
        EntityPlayerMP entityPlayer = CommandRaid.func_71521_c((ICommandSender)sender);
        VillageManager vm = VillageManager.get(entityPlayer.field_70170_p);
        Village village = vm.getVillageAt(entityPlayer.func_180425_c());
        if (village != null) {
            village.forceRaid(raidLevel);
        }
    }
}

