/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.entity.passive.EntityVillager
 *  net.minecraft.init.Items
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.structure.MapGenStructureIO
 *  net.minecraft.world.gen.structure.StructureBoundingBox
 *  net.minecraftforge.fml.common.registry.VillagerRegistry
 *  net.minecraftforge.fml.common.registry.VillagerRegistry$IVillageCreationHandler
 */
package net.tangotek.tektopia.generation;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.entities.EntityBard;
import net.tangotek.tektopia.entities.EntityBlacksmith;
import net.tangotek.tektopia.entities.EntityCleric;
import net.tangotek.tektopia.entities.EntityDruid;
import net.tangotek.tektopia.entities.EntityFarmer;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityLumberjack;
import net.tangotek.tektopia.entities.EntityNitwit;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.generation.TekHouse2;
import net.tangotek.tektopia.generation.TekHouse2Handler;
import net.tangotek.tektopia.generation.TekHouse2b;
import net.tangotek.tektopia.generation.TekHouse2bHandler;
import net.tangotek.tektopia.generation.TekHouse6;
import net.tangotek.tektopia.generation.TekHouse6Handler;
import net.tangotek.tektopia.generation.TekStorageHall;
import net.tangotek.tektopia.generation.TekStorageHallHandler;
import net.tangotek.tektopia.generation.TekTownHall;
import net.tangotek.tektopia.generation.TekTownHallHandler;
import net.tangotek.tektopia.structures.VillageStructureType;

public class TekStructureVillagePieces {
    public static void registerVillagePieces() {
        System.out.println("Registering Tek Village Structures");
        MapGenStructureIO.func_143031_a(TekTownHall.class, (String)"TekTownHall");
        MapGenStructureIO.func_143031_a(TekStorageHall.class, (String)"TekStorage");
        MapGenStructureIO.func_143031_a(TekHouse6.class, (String)"TekHouse6");
        MapGenStructureIO.func_143031_a(TekHouse2.class, (String)"TekHouse2");
        MapGenStructureIO.func_143031_a(TekHouse2b.class, (String)"TekHouse2b");
        VillagerRegistry.instance().registerVillageCreationHandler((VillagerRegistry.IVillageCreationHandler)new TekTownHallHandler());
        VillagerRegistry.instance().registerVillageCreationHandler((VillagerRegistry.IVillageCreationHandler)new TekStorageHallHandler());
        VillagerRegistry.instance().registerVillageCreationHandler((VillagerRegistry.IVillageCreationHandler)new TekHouse6Handler());
        VillagerRegistry.instance().registerVillageCreationHandler((VillagerRegistry.IVillageCreationHandler)new TekHouse2Handler());
        VillagerRegistry.instance().registerVillageCreationHandler((VillagerRegistry.IVillageCreationHandler)new TekHouse2bHandler());
    }

    public static EntityItemFrame addStructureFrame(World worldIn, StructureBoundingBox bbox, BlockPos doorPos, VillageStructureType structType) {
        EnumFacing facing = BlockDoor.func_176517_h((IBlockAccess)worldIn, (BlockPos)doorPos).func_176734_d();
        BlockPos framePos = doorPos.func_177967_a(facing, 1).func_177967_a(facing.func_176746_e(), 1).func_177984_a();
        if (bbox.func_175898_b((Vec3i)framePos)) {
            EntityItemFrame itemFrame = new EntityItemFrame(worldIn, framePos, facing);
            itemFrame.func_82334_a(structType.itemStack);
            worldIn.func_72838_d((Entity)itemFrame);
            return itemFrame;
        }
        return null;
    }

    public static void replaceVillagers(World world, Random ran, double x1, double y1, double z1, double x2, double y2, double z2) {
        List villagerList = world.func_72872_a(EntityVillager.class, new AxisAlignedBB(x1, y1, z1, x2, y2, z2));
        for (EntityVillager vil : villagerList) {
            EntityVillagerTek newVillager = TekStructureVillagePieces.generateVillager(world, ran);
            newVillager.func_82149_j((Entity)vil);
            newVillager.func_180482_a(world.func_175649_E(newVillager.func_180425_c()), null);
            world.func_72838_d((Entity)newVillager);
            vil.func_70106_y();
        }
    }

    public static EntityVillagerTek generateVillager(World world, Random ran) {
        EntityVillagerTek newVillager;
        int rnd = ran.nextInt(80);
        if (rnd < 40) {
            newVillager = new EntityFarmer(world);
        } else if (rnd < 60) {
            newVillager = new EntityLumberjack(world);
            newVillager.getInventory().func_174894_a(ModItems.createTaggedItem(Items.field_151049_t, 1, ItemTagType.VILLAGER));
        } else if (rnd < 70) {
            newVillager = new EntityGuard(world);
            newVillager.getInventory().func_174894_a(ModItems.createTaggedItem(Items.field_151052_q, 1, ItemTagType.VILLAGER));
        } else {
            newVillager = rnd == 70 ? new EntityBard(world) : (rnd == 71 ? new EntityCleric(world) : (rnd == 72 ? new EntityBlacksmith(world) : (rnd == 73 ? new EntityDruid(world) : new EntityNitwit(world))));
        }
        return newVillager;
    }
}

