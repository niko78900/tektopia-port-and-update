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

class CommandReport
extends CommandVillageBase {
    public CommandReport() {
        super("report");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            throw new WrongUsageException("commands.village.report.usage", new Object[0]);
        }
        String reportType = "all";
        if (args.length == 1) {
            reportType = args[0];
        }
        EntityPlayerMP entityplayer = CommandReport.func_71521_c((ICommandSender)sender);
        VillageManager.get(entityplayer.field_70170_p).villageReport(reportType);
    }
}

