/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.server.MinecraftServer
 *  net.minecraftforge.server.command.CommandTreeBase
 *  net.minecraftforge.server.command.CommandTreeHelp
 *  net.minecraftforge.server.permission.DefaultPermissionLevel
 *  net.minecraftforge.server.permission.PermissionAPI
 */
package net.tangotek.tektopia.commands;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.tangotek.tektopia.commands.CommandAnimals;
import net.tangotek.tektopia.commands.CommandDebug;
import net.tangotek.tektopia.commands.CommandEdgeNodes;
import net.tangotek.tektopia.commands.CommandGoto;
import net.tangotek.tektopia.commands.CommandGraph;
import net.tangotek.tektopia.commands.CommandHappy;
import net.tangotek.tektopia.commands.CommandHunger;
import net.tangotek.tektopia.commands.CommandIntelligence;
import net.tangotek.tektopia.commands.CommandItem;
import net.tangotek.tektopia.commands.CommandKill;
import net.tangotek.tektopia.commands.CommandLevel;
import net.tangotek.tektopia.commands.CommandNearby;
import net.tangotek.tektopia.commands.CommandPopulate;
import net.tangotek.tektopia.commands.CommandRaid;
import net.tangotek.tektopia.commands.CommandReport;
import net.tangotek.tektopia.commands.CommandStart;

public class VillageCommands
extends CommandTreeBase {
    public VillageCommands() {
        super.addSubcommand((ICommand)new CommandStart());
        super.addSubcommand((ICommand)new CommandItem());
        super.addSubcommand((ICommand)new CommandAnimals());
        super.addSubcommand((ICommand)new CommandHappy());
        super.addSubcommand((ICommand)new CommandHunger());
        super.addSubcommand((ICommand)new CommandLevel());
        super.addSubcommand((ICommand)new CommandIntelligence());
        super.addSubcommand((ICommand)new CommandReport());
        super.addSubcommand((ICommand)new CommandNearby());
        super.addSubcommand((ICommand)new CommandTreeHelp((CommandTreeBase)this));
        super.addSubcommand((ICommand)new CommandDebug());
        super.addSubcommand((ICommand)new CommandEdgeNodes());
        super.addSubcommand((ICommand)new CommandKill());
        super.addSubcommand((ICommand)new CommandGoto());
        super.addSubcommand((ICommand)new CommandPopulate());
        super.addSubcommand((ICommand)new CommandRaid());
        super.addSubcommand((ICommand)new CommandGraph());
    }

    public static String commandPrefix() {
        return "tektopia.command.village.";
    }

    public void registerNodes() {
        this.getSubCommands().stream().forEach(c -> PermissionAPI.registerNode((String)(VillageCommands.commandPrefix() + c.func_71517_b()), (DefaultPermissionLevel)DefaultPermissionLevel.OP, (String)c.func_71517_b()));
    }

    public String func_71517_b() {
        return "village";
    }

    public int func_82362_a() {
        return 0;
    }

    public boolean func_184882_a(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.village.usage";
    }
}

