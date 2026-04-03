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

class CommandLevel
extends CommandVillageBase {
    public CommandLevel() {
        super("level");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 2) {
            throw new WrongUsageException("commands.village.level.usage", new Object[0]);
        }
        try {
            int levelMod = Integer.parseInt(args[0]);
            EntityPlayerMP entityPlayer = CommandLevel.func_71521_c((ICommandSender)sender);
            List villagers = entityPlayer.field_70170_p.func_72872_a(EntityVillagerTek.class, entityPlayer.func_174813_aQ().func_72314_b(3.0, 3.0, 3.0));
            if (!villagers.isEmpty()) {
                for (int i = 0; i < levelMod; ++i) {
                    ((EntityVillagerTek)((Object)villagers.get(0))).incrementSkill(((EntityVillagerTek)((Object)villagers.get(0))).getProfessionType());
                }
            }
        }
        catch (NumberFormatException ex) {
            throw new WrongUsageException("commands.village.hunger.usage", new Object[0]);
        }
    }
}

