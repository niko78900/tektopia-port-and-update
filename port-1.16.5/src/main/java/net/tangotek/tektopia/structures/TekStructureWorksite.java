package net.tangotek.tektopia.structures;

import net.minecraft.block.Block;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.registry.TekBlocks;

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
            case HOME2:
            case HOME4:
            case HOME6:
                if (block instanceof BedBlock) {
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
            case BLACKSMITH:
                if (block == Blocks.ANVIL || block == Blocks.CHIPPED_ANVIL || block == Blocks.DAMAGED_ANVIL
                        || block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE || block == Blocks.CRAFTING_TABLE) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case BUTCHER:
                if (block == Blocks.SMOKER || block == Blocks.CRAFTING_TABLE) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case RANCH_PEN:
            case SHEEP_PEN:
            case COW_PEN:
            case PIG_PEN:
            case CHICKEN_COOP:
                if (block instanceof FenceBlock || block instanceof FenceGateBlock || block == Blocks.HAY_BLOCK) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case GUARD_POST:
            case BARRACKS:
                if (block == Blocks.IRON_BARS || block == Blocks.CRAFTING_TABLE || block instanceof BedBlock) {
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
            case TAVERN:
                if (block == Blocks.BARREL || block == Blocks.JUKEBOX || block == Blocks.NOTE_BLOCK
                        || block == Blocks.CRAFTING_TABLE || block == TekBlocks.CHAIR.get()) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case SCHOOL:
                if (block == Blocks.LECTERN || block == Blocks.BOOKSHELF
                        || block == Blocks.CRAFTING_TABLE || block == TekBlocks.CHAIR.get()) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            case LIBRARY:
                if (block == Blocks.LECTERN || block == Blocks.BOOKSHELF || block == Blocks.ENCHANTING_TABLE) {
                    this.addSpecialBlock(block, pos);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void validateStructure() {
        super.validateStructure();
        switch (this.type) {
            case HOME:
                this.requireBedCount(1, "needs at least 1 bed");
                break;
            case HOME2:
                this.requireBedCount(2, "needs at least 2 beds");
                break;
            case HOME4:
                this.requireBedCount(4, "needs at least 4 beds");
                break;
            case HOME6:
                this.requireBedCount(6, "needs at least 6 beds");
                break;
            case MINESHAFT:
                this.requireMineshaftOpening();
                break;
            case STORAGE:
                break;
            case KITCHEN:
                this.requireBlock(Blocks.CRAFTING_TABLE, "needs a crafting table");
                this.requireAnyOf("needs a furnace or smoker", Blocks.FURNACE, Blocks.SMOKER);
                break;
            case BLACKSMITH:
                this.requireAnyOf("needs an anvil", Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL);
                this.requireBlock(Blocks.CRAFTING_TABLE, "needs a crafting table");
                this.requireAnyOf("needs a furnace or blast furnace", Blocks.FURNACE, Blocks.BLAST_FURNACE);
                break;
            case BUTCHER:
                if (this.getFloorTileCount() < 8) {
                    this.addValidationProblem("needs at least 8 floor tiles");
                }
                this.requireAnyOf("needs a smoker or crafting table", Blocks.SMOKER, Blocks.CRAFTING_TABLE);
                break;
            case RANCH_PEN:
            case SHEEP_PEN:
            case COW_PEN:
            case PIG_PEN:
            case CHICKEN_COOP:
                this.requireFenceOrGate("needs fencing or a fence gate");
                this.requireGate("needs a fence gate");
                break;
            case GUARD_POST:
                this.requireBlock(Blocks.IRON_BARS, "needs iron bars as a guard marker");
                break;
            case BARRACKS:
                this.requireAnyBed("needs at least one bed");
                break;
            case MERCHANT_STALL:
                this.requireAnyOf("needs a barrel or chest", Blocks.BARREL, Blocks.CHEST);
                break;
            case TAVERN:
                this.requireAnyOf("needs a barrel, jukebox, or note block", Blocks.BARREL, Blocks.JUKEBOX, Blocks.NOTE_BLOCK);
                break;
            case SCHOOL:
                this.requireAnyOf("needs a lectern or bookshelf", Blocks.LECTERN, Blocks.BOOKSHELF);
                this.requireBlock(TekBlocks.CHAIR.get(), "needs chairs for children");
                break;
            case LIBRARY:
                this.requireBlock(Blocks.BOOKSHELF, "needs bookshelves");
                break;
            default:
                break;
        }
    }

    private void requireBlock(Block block, String message) {
        if (!this.specialBlocks.containsKey(block) || this.specialBlocks.get(block).isEmpty()) {
            this.addValidationProblem(message);
        }
    }

    private void requireAnyOf(String message, Block... blocks) {
        for (Block block : blocks) {
            if (this.specialBlocks.containsKey(block) && !this.specialBlocks.get(block).isEmpty()) {
                return;
            }
        }
        this.addValidationProblem(message);
    }

    private void requireAnyBed(String message) {
        for (Block block : this.specialBlocks.keySet()) {
            if (block instanceof BedBlock) {
                return;
            }
        }
        this.addValidationProblem(message);
    }

    private void requireBedCount(int count, String message) {
        int beds = 0;
        for (Block block : this.specialBlocks.keySet()) {
            if (block instanceof BedBlock) {
                beds += this.specialBlocks.get(block).size();
            }
        }
        if (beds < count) {
            this.addValidationProblem(message);
        }
    }

    private void requireFenceOrGate(String message) {
        for (Block block : this.specialBlocks.keySet()) {
            if (block instanceof FenceBlock || block instanceof FenceGateBlock) {
                return;
            }
        }
        this.addValidationProblem(message);
    }

    private void requireGate(String message) {
        for (Block block : this.specialBlocks.keySet()) {
            if (block instanceof FenceGateBlock) {
                return;
            }
        }
        this.addValidationProblem(message);
    }

    private void requireMineshaftOpening() {
        if (this.hasTwoBlockOpeningBelow(this.doorInside)) {
            return;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (this.hasTwoBlockOpeningBelow(this.doorInside.relative(direction))) {
                return;
            }
        }
        this.addValidationProblem("needs a 1x2 opening below or beside the marker");
    }

    private boolean hasTwoBlockOpeningBelow(BlockPos marker) {
        BlockPos first = marker.below();
        BlockPos second = marker.below(2);
        return this.level.getBlockState(first).isAir() && this.level.getBlockState(second).isAir();
    }
}
