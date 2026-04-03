/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.SoundCategory
 */
package net.tangotek.tektopia.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.tangotek.tektopia.commands.CommandVillageBase;
import net.tangotek.tektopia.structures.VillageStructureType;

class CommandStart
extends CommandVillageBase {
    public CommandStart() {
        super("start");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            throw new WrongUsageException("commands.village.start.usage", new Object[0]);
        }
        EntityPlayerMP entityplayer = CommandStart.func_71521_c((ICommandSender)sender);
        boolean flag = entityplayer.field_71071_by.func_70441_a(VillageStructureType.TOWNHALL.itemStack.func_77946_l());
        if (flag |= entityplayer.field_71071_by.func_70441_a(VillageStructureType.STORAGE.itemStack.func_77946_l())) {
            entityplayer.field_70170_p.func_184148_a((EntityPlayer)null, entityplayer.field_70165_t, entityplayer.field_70163_u, entityplayer.field_70161_v, SoundEvents.field_187638_cR, SoundCategory.PLAYERS, 0.2f, ((entityplayer.func_70681_au().nextFloat() - entityplayer.func_70681_au().nextFloat()) * 0.7f + 1.0f) * 2.0f);
            entityplayer.field_71069_bz.func_75142_b();
        }
    }
}

