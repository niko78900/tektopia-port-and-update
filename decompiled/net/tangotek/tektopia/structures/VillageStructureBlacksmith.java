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

public class VillageStructureBlacksmith
extends VillageStructure {
    protected VillageStructureBlacksmith(World world, Village v, EntityItemFrame itemFrame) {
        super(world, v, itemFrame, VillageStructureType.BLACKSMITH, "Blacksmith");
    }

    @Override
    protected void scanSpecialBlock(BlockPos pos, Block block) {
        if (block == Blocks.field_150467_bQ) {
            this.addSpecialBlock(Blocks.field_150467_bQ, pos);
        } else if (block == Blocks.field_150467_bQ) {
            this.addSpecialBlock(Blocks.field_150467_bQ, pos);
        } else if (block == Blocks.field_150460_al || block == Blocks.field_150470_am) {
            this.addSpecialBlock(Blocks.field_150460_al, pos);
        }
        super.scanSpecialBlock(pos, block);
    }
}

