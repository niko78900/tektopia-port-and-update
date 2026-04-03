/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.blockfinder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.blockfinder.BlockScanner;

public class SugarCaneScanner
extends BlockScanner {
    public SugarCaneScanner(Village v, int scansPerTick) {
        super((Block)Blocks.field_150436_aH, v, scansPerTick);
    }

    public static BlockPos getCaneStalk(World w, BlockPos bp) {
        if (SugarCaneScanner.isCane(w.func_180495_p(bp))) {
            while (SugarCaneScanner.isCane(w.func_180495_p(bp = bp.func_177977_b()))) {
            }
            Block downBlock = w.func_180495_p(bp.func_177977_b()).func_177230_c();
            if (downBlock == Blocks.field_150426_aN) {
                return null;
            }
            if (SugarCaneScanner.isCane(w.func_180495_p(bp.func_177981_b(2)))) {
                return bp.func_177984_a();
            }
        }
        return null;
    }

    @Override
    public BlockPos testBlock(World w, BlockPos bp) {
        return SugarCaneScanner.getCaneStalk(w, bp);
    }

    @Override
    public void scanNearby(BlockPos bp) {
        for (BlockPos scanPos : BlockPos.func_191532_a((int)(bp.func_177958_n() - 2), (int)bp.func_177956_o(), (int)(bp.func_177952_p() - 2), (int)(bp.func_177958_n() + 2), (int)bp.func_177956_o(), (int)(bp.func_177952_p() + 2))) {
            this.scanBlock(scanPos);
        }
    }

    public static boolean isCane(IBlockState blockState) {
        return blockState.func_177230_c() == Blocks.field_150436_aH;
    }
}

