/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.EnumHand
 */
package net.tangotek.tektopia.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.commands.CommandVillageBase;

class CommandStructure
extends CommandVillageBase {
    public CommandStructure() {
        super("structure");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            throw new WrongUsageException("commands.village.structure.usage", new Object[0]);
        }
        EntityPlayerMP entityplayer = CommandStructure.func_71521_c((ICommandSender)sender);
        ModItems.makeTaggedItem(entityplayer.func_184586_b(EnumHand.MAIN_HAND), ItemTagType.STRUCTURE);
    }
}

