package net.tangotek.tektopia.village;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.structures.TekVillageStructure;

public class TekVillageStructureManager {
    private final Map<TekStructureType, TekVillageStructure> structures =
            new EnumMap<>(TekStructureType.class);
    private RegistryKey<World> dimension;

    public TekVillageStructure scanStructure(ServerWorld level, TekStructureType type, BlockPos doorInside, Direction signFacing) {
        this.dimension = level.dimension();
        TekVillageStructure structure = type.create(level, doorInside, signFacing);
        structure.rescan();
        this.structures.put(type, structure);
        return structure;
    }

    public Optional<TekVillageStructure> getStructure(TekStructureType type) {
        return Optional.ofNullable(this.structures.get(type));
    }

    public Collection<TekVillageStructure> getStructures() {
        return Collections.unmodifiableCollection(this.structures.values());
    }

    public Optional<RegistryKey<World>> getDimension() {
        return Optional.ofNullable(this.dimension);
    }

    public void clear() {
        this.structures.clear();
        this.dimension = null;
    }
}
