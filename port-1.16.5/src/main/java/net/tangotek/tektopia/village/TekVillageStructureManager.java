package net.tangotek.tektopia.village;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
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
    private final Map<TekStructureType, StructureCacheEntry> structureCache =
            new EnumMap<>(TekStructureType.class);
    private final Map<UUID, TekStructureType> frameAssignments = new HashMap<>();
    private RegistryKey<World> dimension;

    public TekVillageStructure scanStructure(ServerWorld level, TekStructureType type, BlockPos doorInside, Direction signFacing) {
        this.dimension = level.dimension();
        TekVillageStructure structure = type.create(level, doorInside, signFacing);
        structure.rescan();
        this.structures.put(type, structure);
        this.structureCache.put(type, new StructureCacheEntry(type, structure.getDoorInside(), structure.getSignFacing()));
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
        this.structureCache.clear();
        this.frameAssignments.clear();
        this.dimension = null;
    }

    public Map<UUID, TekStructureType> getFrameAssignments() {
        return Collections.unmodifiableMap(this.frameAssignments);
    }

    public CompoundNBT save(CompoundNBT nbt) {
        ListNBT structuresTag = new ListNBT();
        for (StructureCacheEntry entry : this.structureCache.values()) {
            CompoundNBT structureTag = new CompoundNBT();
            structureTag.putString("type", entry.type.name());
            structureTag.putLong("doorInside", entry.doorInside.asLong());
            structureTag.putString("signFacing", entry.signFacing.getName());
            structuresTag.add(structureTag);
        }
        nbt.put("structures", structuresTag);

        ListNBT assignmentsTag = new ListNBT();
        for (Map.Entry<UUID, TekStructureType> assignment : this.frameAssignments.entrySet()) {
            CompoundNBT assignmentTag = new CompoundNBT();
            assignmentTag.putUUID("frameId", assignment.getKey());
            assignmentTag.putString("type", assignment.getValue().name());
            assignmentsTag.add(assignmentTag);
        }
        nbt.put("frameAssignments", assignmentsTag);
        return nbt;
    }

    public void load(ServerWorld level, CompoundNBT nbt) {
        this.clear();
        this.dimension = level.dimension();

        ListNBT structuresTag = nbt.getList("structures", 10);
        for (int i = 0; i < structuresTag.size(); i++) {
            CompoundNBT structureTag = structuresTag.getCompound(i);
            TekStructureType type = this.parseStructureType(structureTag.getString("type"));
            if (type == null || !structureTag.contains("doorInside", 4)) {
                continue;
            }
            BlockPos doorInside = BlockPos.of(structureTag.getLong("doorInside"));
            Direction signFacing = this.parseDirection(structureTag.getString("signFacing"));
            this.scanStructure(level, type, doorInside, signFacing);
        }

        ListNBT assignmentsTag = nbt.getList("frameAssignments", 10);
        for (int i = 0; i < assignmentsTag.size(); i++) {
            CompoundNBT assignmentTag = assignmentsTag.getCompound(i);
            if (!assignmentTag.hasUUID("frameId")) {
                continue;
            }
            TekStructureType type = this.parseStructureType(assignmentTag.getString("type"));
            if (type == null) {
                continue;
            }
            this.frameAssignments.put(assignmentTag.getUUID("frameId"), type);
        }
    }

    private TekStructureType parseStructureType(String name) {
        try {
            return TekStructureType.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private Direction parseDirection(String name) {
        Direction direction = Direction.byName(name);
        return direction == null ? Direction.NORTH : direction;
    }

    private static final class StructureCacheEntry {
        private final TekStructureType type;
        private final BlockPos doorInside;
        private final Direction signFacing;

        private StructureCacheEntry(TekStructureType type, BlockPos doorInside, Direction signFacing) {
            this.type = type;
            this.doorInside = doorInside;
            this.signFacing = signFacing;
        }
    }
}
