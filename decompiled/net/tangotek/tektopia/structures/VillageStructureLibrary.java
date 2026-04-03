/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class VillageStructureLibrary
extends VillageStructure {
    protected VillageStructureLibrary(World world, Village v, EntityItemFrame itemFrame) {
        super(world, v, itemFrame, VillageStructureType.LIBRARY, "Library");
    }

    @Override
    protected void scanSpecialBlock(BlockPos pos, Block block) {
        if (block == Blocks.field_150381_bn) {
            this.addSpecialBlock(Blocks.field_150381_bn, pos);
        } else if (block == Blocks.field_150342_X) {
            this.addSpecialBlock(Blocks.field_150342_X, pos);
        } else if (block == Blocks.field_150462_ai) {
            this.addSpecialBlock(Blocks.field_150462_ai, pos);
        }
        super.scanSpecialBlock(pos, block);
    }

    @Override
    public void update() {
        super.update();
    }
}

