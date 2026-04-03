/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.commands;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.commands.CommandVillageBase;
import net.tangotek.tektopia.entities.EntityBard;
import net.tangotek.tektopia.entities.EntityBlacksmith;
import net.tangotek.tektopia.entities.EntityButcher;
import net.tangotek.tektopia.entities.EntityChef;
import net.tangotek.tektopia.entities.EntityChild;
import net.tangotek.tektopia.entities.EntityCleric;
import net.tangotek.tektopia.entities.EntityDruid;
import net.tangotek.tektopia.entities.EntityEnchanter;
import net.tangotek.tektopia.entities.EntityFarmer;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityLumberjack;
import net.tangotek.tektopia.entities.EntityMiner;
import net.tangotek.tektopia.entities.EntityRancher;
import net.tangotek.tektopia.entities.EntityTeacher;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureRancherPen;
import net.tangotek.tektopia.structures.VillageStructureType;

class CommandPopulate
extends CommandVillageBase {
    public CommandPopulate() {
        super("populate");
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            throw new WrongUsageException("commands.village.populate.usage", new Object[0]);
        }
        EntityPlayerMP entityPlayer = CommandPopulate.func_71521_c((ICommandSender)sender);
        VillageManager vm = VillageManager.get(entityPlayer.field_70170_p);
        Village village = vm.getVillageAt(entityPlayer.func_180425_c());
        if (village != null) {
            int skill = 1;
            if (args.length == 1) {
                skill = Math.min(Integer.valueOf(args[0]), 100);
            }
            this.populateVillagers(village, skill);
            this.populateAnimals(village);
        }
    }

    private void populateAnimals(Village village) {
        this.populateAnimalPen(village, VillageStructureType.COW_PEN);
        this.populateAnimalPen(village, VillageStructureType.PIG_PEN);
        this.populateAnimalPen(village, VillageStructureType.CHICKEN_COOP);
        this.populateAnimalPen(village, VillageStructureType.SHEEP_PEN);
    }

    private void populateAnimalPen(Village v, VillageStructureType penType) {
        List<VillageStructure> pens = v.getStructures(penType);
        for (VillageStructure pen : pens) {
            if (!(pen instanceof VillageStructureRancherPen)) continue;
            VillageStructureRancherPen rancherPen = (VillageStructureRancherPen)pen;
            List animals = rancherPen.getEntitiesInside(rancherPen.getAnimalClass());
            animals.stream().forEach(a -> a.func_70106_y());
            for (int i = 0; i < 12; ++i) {
                rancherPen.spawnAnimal(rancherPen.getDoorInside());
            }
        }
    }

    private void populateVillagers(Village village, int skill) {
        List villagers = village.getWorld().func_72872_a(EntityVillagerTek.class, village.getAABB().func_72314_b(100.0, 100.0, 100.0));
        for (EntityVillagerTek villager : villagers) {
            villager.func_70106_y();
        }
        List<VillageStructure> structs = village.getStructures(VillageStructureType.TOWNHALL);
        if (!structs.isEmpty()) {
            this.spawnVillager(village, skill, new EntityFarmer(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityFarmer(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityFarmer(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityFarmer(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityFarmer(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityFarmer(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityRancher(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityRancher(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityRancher(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityButcher(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityBlacksmith(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityMiner(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityMiner(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityMiner(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityTeacher(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityChild(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityChild(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityChild(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityGuard(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityGuard(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityGuard(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityGuard(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityGuard(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityBard(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityChef(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityEnchanter(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityDruid(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityCleric(village.getWorld()), structs.get(0).getRandomFloorTile());
            this.spawnVillager(village, skill, new EntityLumberjack(village.getWorld()), structs.get(0).getRandomFloorTile());
        }
    }

    private void spawnVillager(Village v, int skill, EntityVillagerTek villager, BlockPos spawnPos) {
        villager.func_70012_b((double)spawnPos.func_177958_n() + 0.5, spawnPos.func_177956_o(), (double)spawnPos.func_177952_p() + 0.5, 0.0f, 0.0f);
        villager.func_180482_a(v.getWorld().func_175649_E(spawnPos), null);
        if (villager.getProfessionType().canCopy) {
            villager.setSkill(villager.getProfessionType(), skill + (int)(villager.func_70681_au().nextGaussian() * 30.0));
        }
        villager.setIntelligence(skill);
        v.getWorld().func_72838_d((Entity)villager);
    }
}

