/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.blockfinder;

import javax.annotation.Nullable;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.blockfinder.BlockScanner;

public class TreeScanner
extends BlockScanner {
    public TreeScanner(Village v, int scansPerTick) {
        super(Blocks.field_150364_r, v, scansPerTick);
    }

    @Override
    public BlockPos testBlock(World w, BlockPos bp) {
        IBlockState blockState = w.func_180495_p(bp);
        if (TreeScanner.isLeaf(blockState)) {
            return this.findTreeFromLeaf(w, bp);
        }
        return null;
    }

    @Override
    public void scanNearby(BlockPos bp) {
        for (BlockPos scanPos : BlockPos.func_191532_a((int)(bp.func_177958_n() - 7), (int)(bp.func_177956_o() + 2), (int)(bp.func_177952_p() - 7), (int)(bp.func_177958_n() + 7), (int)(bp.func_177956_o() + 2), (int)(bp.func_177952_p() + 7))) {
            this.scanBlock(scanPos);
        }
    }

    @Nullable
    protected BlockPos findTreeFromLeaf(World world, BlockPos leafPos) {
        for (BlockPos bp : BlockPos.func_191532_a((int)(leafPos.func_177958_n() - 2), (int)(leafPos.func_177956_o() - 1), (int)(leafPos.func_177952_p() - 2), (int)(leafPos.func_177958_n() + 2), (int)(leafPos.func_177956_o() - 1), (int)(leafPos.func_177952_p() + 2))) {
            BlockPos treePos = TreeScanner.treeTest(world, bp);
            if (treePos == null) continue;
            return treePos;
        }
        return null;
    }

    public static BlockPos treeTest(World world, BlockPos bp) {
        while (TreeScanner.isLog(world.func_180495_p(bp))) {
            if (world.func_180495_p(bp = bp.func_177977_b()).func_177230_c() != Blocks.field_150346_d) continue;
            BlockPos treePos = bp.func_177984_a();
            bp = bp.func_177981_b(3);
            for (int i = 0; i < 9; ++i) {
                IBlockState westBlock = world.func_180495_p(bp.func_177976_e());
                IBlockState eastBlock = world.func_180495_p(bp.func_177974_f());
                if ((TreeScanner.isLeaf(westBlock) || TreeScanner.isLog(westBlock)) && (TreeScanner.isLeaf(eastBlock) || TreeScanner.isLog(eastBlock))) {
                    return treePos;
                }
                bp = bp.func_177984_a();
            }
            return null;
        }
        return null;
    }

    public static boolean isLog(IBlockState blockState) {
        return blockState.func_177230_c() == Blocks.field_150364_r || blockState.func_177230_c() == Blocks.field_150363_s;
    }

    public static boolean isLeaf(IBlockState blockState) {
        return (blockState.func_177230_c() == Blocks.field_150362_t || blockState.func_177230_c() == Blocks.field_150361_u) && (Boolean)blockState.func_177229_b((IProperty)BlockLeaves.field_176237_a) != false;
    }
}

