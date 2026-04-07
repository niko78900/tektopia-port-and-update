package net.tangotek.tektopia.structures;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TekStructureTownHall extends TekVillageStructure {
    public TekStructureTownHall(ServerWorld level, BlockPos doorInside, Direction signFacing) {
        super(level, TekStructureType.TOWNHALL, doorInside, signFacing);
    }
}
