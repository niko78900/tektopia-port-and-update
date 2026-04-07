package net.tangotek.tektopia.structures;

import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public enum TekStructureType {
    TOWNHALL("Town Hall"),
    STORAGE("Storage");

    private final String displayName;

    TekStructureType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public TekVillageStructure create(ServerWorld level, BlockPos doorInside, Direction signFacing) {
        switch (this) {
            case TOWNHALL:
                return new TekStructureTownHall(level, doorInside, signFacing);
            case STORAGE:
                return new TekStructureStorage(level, doorInside, signFacing);
            default:
                throw new IllegalStateException("Unhandled structure type: " + this);
        }
    }

    @Nullable
    public static TekStructureType fromInput(String input) {
        if (input == null) {
            return null;
        }
        String normalized = input.trim().toLowerCase();
        switch (normalized) {
            case "townhall":
            case "town_hall":
            case "town-hall":
                return TOWNHALL;
            case "storage":
                return STORAGE;
            default:
                return null;
        }
    }
}
