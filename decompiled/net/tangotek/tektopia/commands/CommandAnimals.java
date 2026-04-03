/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 */
package net.tangotek.tektopia.commands;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.tangotek.tektopia.EntityTagType;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.commands.CommandVillageBase;

class CommandAnimals
extends CommandVillageBase {
    public CommandAnimals() {
        super("animals");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            throw new WrongUsageException("commands.village.animals.usage", new Object[0]);
        }
        EntityPlayerMP entityplayer = CommandAnimals.func_71521_c((ICommandSender)sender);
        List animals = entityplayer.field_70170_p.func_72872_a(EntityAnimal.class, entityplayer.func_174813_aQ().func_72314_b(15.0, 16.0, 15.0));
        for (EntityAnimal animal : animals) {
            ModEntities.makeTaggedEntity((Entity)animal, EntityTagType.VILLAGER);
        }
    }
}

