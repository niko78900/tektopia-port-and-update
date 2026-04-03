/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureBarracks;
import net.tangotek.tektopia.structures.VillageStructureBlacksmith;
import net.tangotek.tektopia.structures.VillageStructureButcher;
import net.tangotek.tektopia.structures.VillageStructureChickenCoop;
import net.tangotek.tektopia.structures.VillageStructureCowPen;
import net.tangotek.tektopia.structures.VillageStructureGuardPost;
import net.tangotek.tektopia.structures.VillageStructureHome;
import net.tangotek.tektopia.structures.VillageStructureKitchen;
import net.tangotek.tektopia.structures.VillageStructureLibrary;
import net.tangotek.tektopia.structures.VillageStructureMerchantStall;
import net.tangotek.tektopia.structures.VillageStructureMineshaft;
import net.tangotek.tektopia.structures.VillageStructurePigPen;
import net.tangotek.tektopia.structures.VillageStructureSchool;
import net.tangotek.tektopia.structures.VillageStructureSheepPen;
import net.tangotek.tektopia.structures.VillageStructureStorage;
import net.tangotek.tektopia.structures.VillageStructureTavern;
import net.tangotek.tektopia.structures.VillageStructureTownHall;

public enum VillageStructureType {
    STORAGE(new ItemStack((Item)ModItems.structureStorage).func_151001_c("Storage"), 12){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureStorage(w, v, i);
        }
    }
    ,
    BLACKSMITH(new ItemStack((Item)ModItems.structureBlacksmith).func_151001_c("Blacksmith"), 12){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureBlacksmith(w, v, i);
        }
    }
    ,
    MINESHAFT(new ItemStack((Item)ModItems.structureMineshaft).func_151001_c("Mineshaft"), 0){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureMineshaft(w, v, i);
        }
    }
    ,
    TOWNHALL(new ItemStack((Item)ModItems.structureTownHall).func_151001_c("Town Hall"), 12){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureTownHall(w, v, i);
        }
    }
    ,
    COW_PEN(new ItemStack((Item)ModItems.structureCowPen).func_151001_c("Cow Pen"), 0){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureCowPen(w, v, i);
        }
    }
    ,
    SHEEP_PEN(new ItemStack((Item)ModItems.structureSheepPen).func_151001_c("Sheep Pen"), 0){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureSheepPen(w, v, i);
        }
    }
    ,
    PIG_PEN(new ItemStack((Item)ModItems.structurePigPen).func_151001_c("Pig Pen"), 0){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructurePigPen(w, v, i);
        }
    }
    ,
    CHICKEN_COOP(new ItemStack((Item)ModItems.structureChickenCoop).func_151001_c("Chicken Coop"), 0){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureChickenCoop(w, v, i);
        }
    }
    ,
    BUTCHER(new ItemStack((Item)ModItems.structureButcher).func_151001_c("Butcher"), 12){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureButcher(w, v, i);
        }
    }
    ,
    TAVERN(new ItemStack((Item)ModItems.structureTavern).func_151001_c("Tavern"), 0){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureTavern(w, v, i);
        }
    }
    ,
    LIBRARY(new ItemStack((Item)ModItems.structureLibrary).func_151001_c("Library"), 12){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureLibrary(w, v, i);
        }
    }
    ,
    SCHOOL(new ItemStack((Item)ModItems.structureSchool).func_151001_c("School"), 12){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureSchool(w, v, i);
        }
    }
    ,
    HOME2(new ItemStack((Item)ModItems.structureHome2).func_151001_c("Home2"), 12){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureHome(w, v, i, HOME2, "Home", 2);
        }
    }
    ,
    HOME4(new ItemStack((Item)ModItems.structureHome4).func_151001_c("Home4"), 12){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureHome(w, v, i, HOME4, "Home", 4);
        }
    }
    ,
    HOME6(new ItemStack((Item)ModItems.structureHome6).func_151001_c("Home6"), 12){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureHome(w, v, i, HOME6, "Home", 6);
        }
    }
    ,
    GUARD_POST(new ItemStack((Item)ModItems.structureGuardPost).func_151001_c("Guard Post"), 0){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureGuardPost(w, v, i);
        }
    }
    ,
    MERCHANT_STALL(new ItemStack((Item)ModItems.structureMerchantStall).func_151001_c("Merchant Stall"), 0){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureMerchantStall(w, v, i);
        }
    }
    ,
    BARRACKS(new ItemStack((Item)ModItems.structureBarracks).func_151001_c("Barracks"), 12){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureBarracks(w, v, i);
        }
    }
    ,
    KITCHEN(new ItemStack((Item)ModItems.structureKitchen).func_151001_c("Kitchen"), 12){

        @Override
        public VillageStructure create(World w, Village v, EntityItemFrame i) {
            return new VillageStructureKitchen(w, v, i);
        }
    };

    public static final String TOWN_HALL_NAME = "Town Hall";
    public final ItemStack itemStack;
    public final int tilesPerVillager;

    public boolean isHome() {
        return this == HOME2 || this == HOME4 || this == HOME6;
    }

    private VillageStructureType(ItemStack i, int tilesPer) {
        this.itemStack = i;
        this.tilesPerVillager = tilesPer;
    }

    public boolean isItemEqual(ItemStack i) {
        return this.itemStack.func_77969_a(i);
    }

    public abstract VillageStructure create(World var1, Village var2, EntityItemFrame var3);
}

