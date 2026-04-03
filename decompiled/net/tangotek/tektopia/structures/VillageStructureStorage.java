/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.init.Blocks
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class VillageStructureStorage
extends VillageStructure {
    protected Map<BlockPos, TileEntityChest> storageBlocks;
    protected Map<BlockPos, TileEntityChest> newStorageBlocks;

    protected VillageStructureStorage(World world, Village v, EntityItemFrame itemFrame) {
        super(world, v, itemFrame, VillageStructureType.STORAGE, "Storage");
    }

    @Override
    protected void onFloorScanStart() {
        this.newStorageBlocks = new HashMap<BlockPos, TileEntityChest>();
    }

    @Override
    protected void scanSpecialBlock(BlockPos pos, Block block) {
        TileEntity te = this.world.func_175625_s(pos);
        if (te instanceof TileEntityChest) {
            this.newStorageBlocks.put(pos, (TileEntityChest)te);
            this.specialAdded = true;
        } else if (block == Blocks.field_150462_ai) {
            this.addSpecialBlock(Blocks.field_150462_ai, pos);
        } else if ((block == Blocks.field_150460_al || block == Blocks.field_150470_am) && this.getUnoccupiedSpecialBlock(Blocks.field_150460_al) == null) {
            this.addSpecialBlock(Blocks.field_150460_al, pos);
        }
        super.scanSpecialBlock(pos, block);
    }

    @Override
    protected void onFloorScanEnd() {
        if (this.storageBlocks != null) {
            HashSet<BlockPos> removedKeys = new HashSet<BlockPos>(this.storageBlocks.keySet());
            removedKeys.removeAll(this.newStorageBlocks.keySet());
            removedKeys.forEach(bp -> this.village.removeStorageChest(this.storageBlocks.get(bp)));
            HashSet<BlockPos> addedKeys = new HashSet<BlockPos>(this.newStorageBlocks.keySet());
            addedKeys.removeAll(this.storageBlocks.keySet());
            addedKeys.forEach(bp -> this.village.addStorageChest(this.newStorageBlocks.get(bp)));
        } else {
            this.village.resetStorage();
            this.newStorageBlocks.values().forEach(chest -> this.village.addStorageChest((TileEntityChest)chest));
        }
        this.storageBlocks = this.newStorageBlocks;
    }

    @Override
    public void onDestroy() {
        this.storageBlocks.values().forEach(chest -> this.village.removeStorageChest((TileEntityChest)chest));
        super.onDestroy();
    }

    @Override
    public int getMaxAllowed() {
        return 1;
    }
}

