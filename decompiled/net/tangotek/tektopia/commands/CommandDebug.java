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
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.commands.CommandVillageBase;

class CommandDebug
extends CommandVillageBase {
    public CommandDebug() {
        super("debug");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean enable = false;
        if (args.length != 1) {
            throw new WrongUsageException("commands.village.debug.usage", new Object[0]);
        }
        enable = Boolean.parseBoolean(args[0]);
        EntityPlayerMP entityplayer = CommandDebug.func_71521_c((ICommandSender)sender);
        VillageManager.get(entityplayer.field_70170_p).setDebugOn(enable);
    }
}

