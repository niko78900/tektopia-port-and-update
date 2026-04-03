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

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.tangotek.tektopia.commands.CommandVillageBase;
import net.tangotek.tektopia.entities.EntityVillageNavigator;

class CommandKill
extends CommandVillageBase {
    public CommandKill() {
        super("kill");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            throw new WrongUsageException("commands.village.kill.usage", new Object[0]);
        }
        EntityPlayerMP entityPlayer = CommandKill.func_71521_c((ICommandSender)sender);
        List villagers = entityPlayer.field_70170_p.func_72872_a(EntityVillageNavigator.class, entityPlayer.func_174813_aQ().func_72314_b(200.0, 200.0, 200.0));
        for (EntityVillageNavigator villager : villagers) {
            villager.func_70106_y();
        }
    }
}

