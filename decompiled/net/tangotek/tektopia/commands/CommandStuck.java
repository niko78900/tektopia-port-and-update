/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.commands.CommandVillageBase;

class CommandStuck
extends CommandVillageBase {
    public CommandStuck() {
        super("stuck");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            throw new WrongUsageException("commands.village.stuck.usage", new Object[0]);
        }
        EntityPlayerMP entityplayer = CommandStuck.func_71521_c((ICommandSender)sender);
        BlockPos stuckPos = VillageManager.get(entityplayer.field_70170_p).getLastStuck();
        if (stuckPos != null && entityplayer instanceof EntityPlayerMP) {
            entityplayer.field_71135_a.func_147364_a((double)stuckPos.func_177958_n(), (double)stuckPos.func_177956_o(), (double)stuckPos.func_177952_p(), 0.0f, 0.0f);
        }
    }
}

