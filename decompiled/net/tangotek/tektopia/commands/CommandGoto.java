/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 */
package net.tangotek.tektopia.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.tangotek.tektopia.commands.CommandVillageBase;

class CommandGoto
extends CommandVillageBase {
    public CommandGoto() {
        super("goto");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException("commands.village.goto.usage", new Object[0]);
        }
        EntityPlayerMP entityPlayer = CommandGoto.func_71521_c((ICommandSender)sender);
        Entity e = entityPlayer.field_70170_p.func_73045_a(Integer.valueOf(args[0]).intValue());
        if (e != null && entityPlayer instanceof EntityPlayerMP) {
            entityPlayer.field_71135_a.func_147364_a(e.field_70165_t, e.field_70163_u, e.field_70161_v, 0.0f, 0.0f);
        }
    }
}

