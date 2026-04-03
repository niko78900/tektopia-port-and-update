/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.commands;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.commands.CommandVillageBase;

class CommandNearby
extends CommandVillageBase {
    public CommandNearby() {
        super("nearby");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            throw new WrongUsageException("commands.village.nearby.usage", new Object[0]);
        }
        EntityPlayerMP entityplayer = CommandNearby.func_71521_c((ICommandSender)sender);
        Integer range = 360;
        List<Village> villages = VillageManager.get(entityplayer.field_70170_p).getVillagesNear(entityplayer.func_180425_c(), range);
        if (villages.isEmpty()) {
            CommandNearby.func_152373_a((ICommandSender)sender, (ICommand)this, (String)"commands.nearby.none", (Object[])new Object[]{range});
        } else {
            BlockPos p = entityplayer.func_180425_c();
            villages.stream().forEach(v -> CommandNearby.func_152373_a((ICommandSender)sender, (ICommand)this, (String)"commands.nearby.set", (Object[])new Object[]{range, v.getOrigin(), v.getOrigin().func_185332_f(p.func_177958_n(), p.func_177956_o(), p.func_177952_p())}));
        }
    }
}

