package net.tangotek.tektopia.structures;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TekStructureWorksite extends TekVillageStructure {
    public TekStructureWorksite(ServerWorld level, TekStructureType type, BlockPos doorInside, Direction signFacing) {
        super(level, type, doorInside, signFacing);
    }

    @Override
    protected void scanSpecialBlock(BlockPos pos, Block block) {
        TileEntity blockEntity = this.level.getBlockEntity(pos);
        if (blockEntity instanceof ChestTileEntity) {
            this.addSpecialBlock(block, pos);
            return;
        }

        switch (this.type) {
            case HOME:
                if (block == Blocks.WHITE_BED || block == Blocks.RED_BED || block == Blocks.BLUE_BED || block == Blocks.GREEN_BED) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case MINESHAFT:
                if (block == Blocks.STONECUTTER || block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case LUMBER_AREA:
                if (block == Blocks.OAK_LOG || block == Blocks.SPRUCE_LOG || block == Blocks.BIRCH_LOG || block == Blocks.CRAFTING_TABLE) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case KITCHEN:
                if (block == Blocks.FURNACE || block == Blocks.SMOKER || block == Blocks.CRAFTING_TABLE || block == Blocks.CAULDRON) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case BUTCHER:
                if (block == Blocks.SMOKER || block == Blocks.CRAFTING_TABLE) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case RANCH_PEN:
                if (block == Blocks.OAK_FENCE || block == Blocks.OAK_FENCE_GATE || block == Blocks.HAY_BLOCK) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case GUARD_POST:
            case BARRACKS:
                if (block == Blocks.IRON_BARS || block == Blocks.CRAFTING_TABLE || block == Blocks.WHITE_BED || block == Blocks.RED_BED) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case MERCHANT_STALL:
                if (block == Blocks.BARREL || block == Blocks.CHEST || block == Blocks.CRAFTING_TABLE) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case FARM:
                if (block == Blocks.COMPOSTER || block == Blocks.HAY_BLOCK || block == Blocks.CRAFTING_TABLE) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            default:
                break;
        }
    }
}
