/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockBed
 *  net.minecraft.block.BlockBed$EnumPartType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.structure.StructureBoundingBox
 *  net.minecraft.world.gen.structure.StructureVillagePieces$Start
 *  net.minecraft.world.gen.structure.StructureVillagePieces$WoodHut
 */
package net.tangotek.tektopia.generation;

import java.util.Random;
import net.minecraft.block.BlockBed;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.tangotek.tektopia.generation.TekStructureVillagePieces;
import net.tangotek.tektopia.structures.VillageStructureType;

public class TekHouse2
extends StructureVillagePieces.WoodHut {
    private int villagersSpawned;

    public TekHouse2(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox bbox, EnumFacing facing) {
        super(start, type, rand, bbox, facing);
    }

    public TekHouse2() {
    }

    public boolean func_74875_a(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        boolean result = super.func_74875_a(worldIn, randomIn, structureBoundingBoxIn);
        if (!this.field_189929_i) {
            this.placeBedPiece(worldIn, structureBoundingBoxIn, 2, 1, 2, EnumFacing.NORTH, BlockBed.EnumPartType.FOOT);
            this.placeBedPiece(worldIn, structureBoundingBoxIn, 2, 1, 3, EnumFacing.NORTH, BlockBed.EnumPartType.HEAD);
            this.func_189926_a(worldIn, EnumFacing.SOUTH, 1, 1, 3, structureBoundingBoxIn);
        }
        return result;
    }

    public BlockPos getBlockPos(int x, int y, int z) {
        return new BlockPos(this.func_74865_a(x, z), this.func_74862_a(y), this.func_74873_b(x, z));
    }

    protected void func_189927_a(World w, StructureBoundingBox bb, Random rand, int x, int y, int z, EnumFacing facing) {
        super.func_189927_a(w, bb, rand, x, y, z, facing);
        TekStructureVillagePieces.addStructureFrame(w, bb, this.getBlockPos(x, y, z), VillageStructureType.HOME2);
    }

    private void placeBedPiece(World worldIn, StructureBoundingBox bbox, int x, int y, int z, EnumFacing facing, BlockBed.EnumPartType partType) {
        IBlockState bedState = Blocks.field_150324_C.func_176223_P().func_177226_a((IProperty)BlockBed.field_176471_b, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)BlockBed.field_185512_D, (Comparable)facing);
        this.func_175811_a(worldIn, bedState.func_177226_a((IProperty)BlockBed.field_176472_a, (Comparable)partType), x, y, z, bbox);
        this.func_175811_a(worldIn, Blocks.field_150350_a.func_176223_P(), x, y + 1, z, bbox);
    }

    protected void func_74893_a(World worldIn, StructureBoundingBox structurebb, int x, int y, int z, int count) {
    }
}

