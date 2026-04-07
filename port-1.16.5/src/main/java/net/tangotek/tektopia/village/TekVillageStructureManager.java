package net.tangotek.tektopia.village;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
    private final Map<UUID, TekStructureType> frameAssignments = new HashMap<>();
    private RegistryKey<World> dimension;

    public TekVillageStructure scanStructure(ServerWorld level, TekStructureType type, BlockPos doorInside, Direction signFacing) {
        this.dimension = level.dimension();
        TekVillageStructure structure = type.create(level, doorInside, signFacing);
        structure.rescan();
        this.structures.put(type, structure);
        return structure;
    }

    public int scanStructuresFromFrames(ServerWorld level, BlockPos center, int radius) {
        this.dimension = level.dimension();
        List<TekStructureDiscovery.DiscoveredStructure> discovered =
                TekStructureDiscovery.discoverFromFrames(level, center, radius);
        for (TekStructureDiscovery.DiscoveredStructure candidate : discovered) {
            TekVillageStructure structure = this.scanStructure(
                    level,
                    candidate.getType(),
                    candidate.getDoorInside(),
                    candidate.getSignFacing()
            );
            this.frameAssignments.put(candidate.getFrameId(), structure.getType());
        }
        return discovered.size();
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
        this.frameAssignments.clear();
        this.dimension = null;
    }

    public Map<UUID, TekStructureType> getFrameAssignments() {
        return Collections.unmodifiableMap(this.frameAssignments);
    }
}
