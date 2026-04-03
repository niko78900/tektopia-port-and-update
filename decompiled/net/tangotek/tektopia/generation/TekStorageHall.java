/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.structure.StructureBoundingBox
 *  net.minecraft.world.gen.structure.StructureVillagePieces$House3
 *  net.minecraft.world.gen.structure.StructureVillagePieces$Start
 */
package net.tangotek.tektopia.generation;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.generation.TekStructureVillagePieces;
import net.tangotek.tektopia.structures.VillageStructureType;

public class TekStorageHall
extends StructureVillagePieces.House3 {
    public TekStorageHall(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox bbox, EnumFacing facing) {
        super(start, type, rand, bbox, facing);
    }

    public TekStorageHall() {
    }

    public boolean func_74875_a(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        boolean result = super.func_74875_a(worldIn, randomIn, structureBoundingBoxIn);
        if (!this.field_189929_i) {
            this.func_175811_a(worldIn, Blocks.field_150462_ai.func_176223_P(), 1, 1, 4, structureBoundingBoxIn);
            this.func_189926_a(worldIn, EnumFacing.WEST, 7, 1, 6, structureBoundingBoxIn);
            this.func_186167_a(worldIn, structureBoundingBoxIn, randomIn, 4, 1, 9, TekVillager.VILLAGE_STORAGE);
            this.func_186167_a(worldIn, structureBoundingBoxIn, randomIn, 6, 1, 9, TekVillager.VILLAGE_STORAGE);
            this.func_186167_a(worldIn, structureBoundingBoxIn, randomIn, 2, 1, 4, TekVillager.VILLAGE_STORAGE);
        }
        return result;
    }

    protected void func_74893_a(World worldIn, StructureBoundingBox structurebb, int x, int y, int z, int count) {
    }

    public BlockPos getBlockPos(int x, int y, int z) {
        return new BlockPos(this.func_74865_a(x, z), this.func_74862_a(y), this.func_74873_b(x, z));
    }

    protected void func_189927_a(World w, StructureBoundingBox bb, Random rand, int x, int y, int z, EnumFacing facing) {
        super.func_189927_a(w, bb, rand, x, y, z, facing);
        TekStructureVillagePieces.addStructureFrame(w, bb, this.getBlockPos(x, y, z), VillageStructureType.STORAGE);
    }
}

