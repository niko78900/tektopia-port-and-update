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
import net.tangotek.tektopia.entities.EntityVillagerTek;

class CommandIntelligence
extends CommandVillageBase {
    public CommandIntelligence() {
        super("intelligence");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int intelligenceMod = 0;
        if (args.length > 2) {
            throw new WrongUsageException("commands.village.intelligence.usage", new Object[0]);
        }
        try {
            intelligenceMod = Integer.parseInt(args[0]);
            int ticks = 1;
            if (args.length > 1) {
                ticks = Integer.parseInt(args[1]);
            }
            EntityPlayerMP entityPlayer = CommandIntelligence.func_71521_c((ICommandSender)sender);
            List villagers = entityPlayer.field_70170_p.func_72872_a(EntityVillagerTek.class, entityPlayer.func_174813_aQ().func_72314_b(6.0, 4.0, 6.0));
            for (EntityVillagerTek villager : villagers) {
                villager.addIntelligenceDelay(intelligenceMod, ticks);
            }
        }
        catch (NumberFormatException ex) {
            throw new WrongUsageException("commands.village.intelligence.usage", new Object[0]);
        }
    }
}

