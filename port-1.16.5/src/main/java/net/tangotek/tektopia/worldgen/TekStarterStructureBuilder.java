package net.tangotek.tektopia.worldgen;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.structures.TekStructureType;

public final class TekStarterStructureBuilder {
    private TekStarterStructureBuilder() {
    }

    public static BlockPos placeStarterStructure(ServerWorld level, BlockPos doorInside, Direction facing, TekStructureType type) {
        Direction right = facing.getClockWise();
        int halfWidth = type == TekStructureType.MINESHAFT ? 2 : 3;
        int depth = type == TekStructureType.FARM ? 6 : 4;
        for (int forward = 0; forward < depth; forward++) {
            for (int side = -halfWidth; side <= halfWidth; side++) {
                BlockPos pos = doorInside.relative(facing, forward).relative(right, side);
                level.setBlock(pos.below(), type == TekStructureType.FARM ? Blocks.FARMLAND.defaultBlockState() : Blocks.OAK_PLANKS.defaultBlockState(), 3);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above(2), Blocks.AIR.defaultBlockState(), 3);
            }
        }
        addWorkBlocks(level, doorInside.relative(facing, 2), right, type);
        return doorInside.immutable();
    }

    private static void addWorkBlocks(ServerWorld level, BlockPos center, Direction right, TekStructureType type) {
        switch (type) {
            case STORAGE:
                level.setBlock(center.relative(right), Blocks.CHEST.defaultBlockState(), 3);
                level.setBlock(center.relative(right.getOpposite()), Blocks.CRAFTING_TABLE.defaultBlockState(), 3);
                break;
            case HOME:
                level.setBlock(center.relative(right), Blocks.WHITE_BED.defaultBlockState(), 3);
                break;
            case FARM:
                for (int i = -2; i <= 2; i++) {
                    BlockPos crop = center.relative(right, i);
                    level.setBlock(crop.below(), Blocks.FARMLAND.defaultBlockState(), 3);
                    level.setBlock(crop, Blocks.WHEAT.defaultBlockState(), 3);
                }
                level.setBlock(center.relative(right, 3), Blocks.COMPOSTER.defaultBlockState(), 3);
                break;
            case MINESHAFT:
                level.setBlock(center.relative(right), Blocks.STONECUTTER.defaultBlockState(), 3);
                level.setBlock(center.relative(right.getOpposite()), Blocks.FURNACE.defaultBlockState(), 3);
                level.setBlock(center.relative(right, 2), Blocks.STONE.defaultBlockState(), 3);
                level.setBlock(center.relative(right, -2), Blocks.COAL_ORE.defaultBlockState(), 3);
                break;
            case TOWNHALL:
            default:
                level.setBlock(center.relative(right), Blocks.BELL.defaultBlockState(), 3);
                level.setBlock(center.relative(right.getOpposite()), Blocks.CRAFTING_TABLE.defaultBlockState(), 3);
                break;
        }
    }
}
