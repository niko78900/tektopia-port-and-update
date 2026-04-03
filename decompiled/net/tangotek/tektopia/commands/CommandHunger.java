/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 */
package net.tangotek.tektopia.commands;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.commands.CommandVillageBase;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;

class CommandHunger
extends CommandVillageBase {
    public CommandHunger() {
        super("hunger");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 2) {
            throw new WrongUsageException("commands.village.hunger.usage", new Object[0]);
        }
        try {
            VillageStructure struct;
            int hungerMod = Integer.parseInt(args[0]);
            int ticks = 1;
            if (args.length > 1) {
                ticks = Integer.parseInt(args[1]);
            }
            EntityPlayerMP entityPlayer = CommandHunger.func_71521_c((ICommandSender)sender);
            List villagers = entityPlayer.field_70170_p.func_72872_a(EntityVillagerTek.class, entityPlayer.func_174813_aQ().func_72314_b(12.0, 12.0, 12.0));
            for (EntityVillagerTek villager : villagers) {
                villager.modifyHungerDelay(hungerMod, ticks);
            }
            VillageManager vm = VillageManager.get(entityPlayer.field_70170_p);
            Village village = vm.getVillageAt(entityPlayer.func_180425_c());
            if (village != null && (struct = village.getStructure(entityPlayer.func_180425_c())) != null) {
                List<EntityAnimal> animals = struct.getEntitiesInside(EntityAnimal.class);
                animals.forEach(a -> ModEntities.modifyAnimalHunger(a, hungerMod));
            }
        }
        catch (NumberFormatException ex) {
            throw new WrongUsageException("commands.village.hunger.usage", new Object[0]);
        }
    }
}

