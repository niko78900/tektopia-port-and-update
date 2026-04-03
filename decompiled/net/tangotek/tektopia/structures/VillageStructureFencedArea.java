/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class VillageStructureFencedArea
extends VillageStructure {
    protected VillageStructureFencedArea(World world, Village v, EntityItemFrame itemFrame, VillageStructureType t, String name) {
        super(world, v, itemFrame, t, name);
    }

    @Override
    protected BlockPos findDoor() {
        BlockPos dp = null;
        dp = this.framePos.func_177967_a(this.signFacing, -1).func_177967_a(this.signFacing.func_176746_e(), 1);
        if (!(VillageStructureFencedArea.isWoodDoor(this.world, dp) || VillageStructureFencedArea.isGate(this.world, dp) || VillageStructureFencedArea.isWoodDoor(this.world, dp = this.framePos.func_177967_a(this.signFacing, -1).func_177967_a(this.signFacing.func_176746_e(), -1)) || VillageStructureFencedArea.isGate(this.world, dp) || VillageStructureFencedArea.isWoodDoor(this.world, dp = this.framePos.func_177967_a(this.signFacing, -1).func_177979_c(2)) || VillageStructureFencedArea.isGate(this.world, dp))) {
            dp = null;
        }
        return dp;
    }

    @Override
    protected int scanRoomHeight(BlockPos pos) {
        for (int i = 0; i < 10; ++i) {
            BlockPos p = pos.func_177981_b(i);
            Block b = this.world.func_180495_p(p).func_177230_c();
            this.scanSpecialBlock(p, b);
            if (BasePathingNode.isPassable(this.world, p) && !VillageStructureFencedArea.isWoodDoor(this.world, pos) && !VillageStructureFencedArea.isGate(this.world, pos)) continue;
            return i;
        }
        return 10;
    }
}

