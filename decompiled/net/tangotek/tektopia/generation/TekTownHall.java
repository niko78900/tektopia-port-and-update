/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.structure.StructureBoundingBox
 *  net.minecraft.world.gen.structure.StructureVillagePieces$House1
 *  net.minecraft.world.gen.structure.StructureVillagePieces$Start
 */
package net.tangotek.tektopia.generation;

import java.util.Random;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.tangotek.tektopia.ModBlocks;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.caps.VillageDataProvider;
import net.tangotek.tektopia.generation.TekStructureVillagePieces;
import net.tangotek.tektopia.structures.VillageStructureType;

public class TekTownHall
extends StructureVillagePieces.House1 {
    public TekTownHall(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox p_i45571_4_, EnumFacing facing) {
        super(start, type, rand, p_i45571_4_, facing);
    }

    public TekTownHall() {
    }

    public boolean func_74875_a(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        boolean result = super.func_74875_a(worldIn, randomIn, structureBoundingBoxIn);
        if (!this.field_189929_i) {
            this.func_175811_a(worldIn, Blocks.field_150350_a.func_176223_P(), 6, 1, 4, structureBoundingBoxIn);
            this.func_175811_a(worldIn, Blocks.field_150350_a.func_176223_P(), 5, 1, 4, structureBoundingBoxIn);
            this.func_175811_a(worldIn, Blocks.field_150350_a.func_176223_P(), 4, 1, 4, structureBoundingBoxIn);
            this.func_175811_a(worldIn, Blocks.field_150350_a.func_176223_P(), 3, 1, 4, structureBoundingBoxIn);
            this.func_175811_a(worldIn, Blocks.field_150350_a.func_176223_P(), 7, 1, 4, structureBoundingBoxIn);
            this.func_175811_a(worldIn, Blocks.field_150350_a.func_176223_P(), 7, 1, 3, structureBoundingBoxIn);
            this.func_175811_a(worldIn, Blocks.field_150350_a.func_176223_P(), 7, 1, 2, structureBoundingBoxIn);
            this.func_175811_a(worldIn, ModBlocks.blockChair.func_176223_P().func_177226_a((IProperty)BlockDoor.field_176520_a, (Comparable)EnumFacing.WEST), 7, 1, 3, structureBoundingBoxIn);
            this.func_175811_a(worldIn, ModBlocks.blockChair.func_176223_P().func_177226_a((IProperty)BlockDoor.field_176520_a, (Comparable)EnumFacing.SOUTH), 4, 1, 4, structureBoundingBoxIn);
        }
        return result;
    }

    protected void func_74893_a(World worldIn, StructureBoundingBox structurebb, int x, int y, int z, int count) {
    }

    public BlockPos getBlockPos(int x, int y, int z) {
        return new BlockPos(this.func_74865_a(x, z), this.func_74862_a(y), this.func_74873_b(x, z));
    }

    protected void func_189927_a(World w, StructureBoundingBox bb, Random rand, int x, int y, int z, EnumFacing facing) {
        IVillageData vd;
        super.func_189927_a(w, bb, rand, x, y, z, facing);
        EntityItemFrame itemFrame = TekStructureVillagePieces.addStructureFrame(w, bb, this.getBlockPos(x, y, z), VillageStructureType.TOWNHALL);
        if (itemFrame != null && (vd = (IVillageData)itemFrame.func_82335_i().getCapability(VillageDataProvider.VILLAGE_DATA_CAPABILITY, null)) != null) {
            vd.skipStartingGifts();
        }
    }
}

