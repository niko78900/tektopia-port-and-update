/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.PlayerNotFoundException
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.server.permission.PermissionAPI
 */
package net.tangotek.tektopia.commands;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.PermissionAPI;
import net.tangotek.tektopia.commands.VillageCommands;

abstract class CommandVillageBase
extends CommandBase {
    protected final String name;

    public CommandVillageBase(String n) {
        this.name = n;
    }

    public String func_71517_b() {
        return this.name;
    }

    public List<String> func_71514_a() {
        return Collections.singletonList(this.name);
    }

    public String func_71518_a(ICommandSender sender) {
        return "commands.tektopia." + this.name + ".usage";
    }

    public boolean func_184882_a(MinecraftServer server, ICommandSender sender) {
        try {
            return PermissionAPI.hasPermission((EntityPlayer)CommandVillageBase.func_71521_c((ICommandSender)sender), (String)(VillageCommands.commandPrefix() + this.func_71517_b()));
        }
        catch (PlayerNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int func_82362_a() {
        return 4;
    }

    public List<String> func_184883_a(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }
}

