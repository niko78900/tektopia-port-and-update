package net.tangotek.tektopia.structures;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TekStructureStorage extends TekVillageStructure {
    private final Set<BlockPos> chestPositions = new HashSet<>();

    public TekStructureStorage(ServerWorld level, BlockPos doorInside, Direction signFacing) {
        super(level, TekStructureType.STORAGE, doorInside, signFacing);
    }

    @Override
    protected void onFloorScanStart() {
        this.chestPositions.clear();
    }

    @Override
    protected void scanSpecialBlock(BlockPos pos, Block block) {
        TileEntity blockEntity = this.level.getBlockEntity(pos);
        if (blockEntity instanceof ChestTileEntity) {
            this.chestPositions.add(pos.immutable());
            this.specialAdded = true;
            return;
        }
        if (block == Blocks.CRAFTING_TABLE) {
            this.addSpecialBlock(Blocks.CRAFTING_TABLE, pos);
            return;
        }
        if (block == Blocks.ANVIL || block == Blocks.CHIPPED_ANVIL || block == Blocks.DAMAGED_ANVIL) {
            this.addSpecialBlock(Blocks.ANVIL, pos);
        }
    }

    public Set<BlockPos> getChestPositions() {
        return Collections.unmodifiableSet(this.chestPositions);
    }

    @Override
    protected void validateStructure() {
        super.validateStructure();
        if (this.chestPositions.isEmpty()) {
            this.addValidationProblem("needs at least one chest on the structure floor");
        }
    }
}
