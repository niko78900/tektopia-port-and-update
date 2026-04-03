/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.blockfinder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.blockfinder.BlockScanner;

public class SaplingScanner
extends BlockScanner {
    public SaplingScanner(Village v, int scansPerTick) {
        super(Blocks.field_150345_g, v, scansPerTick);
    }

    @Override
    public BlockPos testBlock(World w, BlockPos bp) {
        IBlockState blockState = w.func_180495_p(bp);
        if (SaplingScanner.isSapling(blockState)) {
            return bp;
        }
        return null;
    }

    @Override
    public void scanNearby(BlockPos bp) {
        for (BlockPos scanPos : BlockPos.func_191532_a((int)(bp.func_177958_n() - 7), (int)(bp.func_177956_o() - 2), (int)(bp.func_177952_p() - 7), (int)(bp.func_177958_n() + 7), (int)(bp.func_177956_o() + 2), (int)(bp.func_177952_p() + 7))) {
            this.scanBlock(scanPos);
        }
    }

    public static boolean isSapling(IBlockState blockState) {
        return blockState.func_177230_c() == Blocks.field_150345_g;
    }
}

