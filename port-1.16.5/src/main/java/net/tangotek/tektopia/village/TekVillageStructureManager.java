package net.tangotek.tektopia.village;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.common.TekItemMeta;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.structures.TekVillageStructure;

public class TekVillageStructureManager {
    private final Map<TekStructureType, TekVillageStructure> structures =
            new EnumMap<>(TekStructureType.class);
    private final Map<TekStructureType, StructureCacheEntry> structureCache =
            new EnumMap<>(TekStructureType.class);
    private final Map<UUID, TekStructureType> frameAssignments = new HashMap<>();
    private RegistryKey<World> dimension;
    private int lastFrameScanCandidates;
    private int lastFrameScanAccepted;
    private int lastFrameScanRejectedBoundTokens;

    public TekVillageStructure scanStructure(ServerWorld level, TekStructureType type, BlockPos doorInside, Direction signFacing) {
        this.dimension = level.dimension();
        TekVillageStructure structure = this.createStructure(level, type, doorInside, signFacing);
        this.cacheStructure(structure);
        return structure;
    }

    private TekVillageStructure createStructure(ServerWorld level, TekStructureType type, BlockPos doorInside, Direction signFacing) {
        TekVillageStructure structure = type.create(level, doorInside, signFacing);
        structure.rescan();
        return structure;
    }

    private void cacheStructure(TekVillageStructure structure) {
        this.structures.put(structure.getType(), structure);
        this.structureCache.put(structure.getType(), new StructureCacheEntry(structure.getType(), structure.getDoorInside(), structure.getSignFacing()));
    }

    public int scanStructuresFromFrames(ServerWorld level, BlockPos center, int radius) {
        this.dimension = level.dimension();
        List<TekStructureDiscovery.DiscoveredStructure> discovered =
                TekStructureDiscovery.discoverFromFrames(level, center, radius);
        if (discovered.size() > 1) {
            discovered.sort((left, right) -> Boolean.compare(
                    right.getType() == TekStructureType.TOWNHALL,
                    left.getType() == TekStructureType.TOWNHALL
            ));
        }
        this.lastFrameScanCandidates = discovered.size();
        this.lastFrameScanAccepted = 0;
        this.lastFrameScanRejectedBoundTokens = 0;
        for (TekStructureDiscovery.DiscoveredStructure candidate : discovered) {
            if (!this.canUseFrameToken(level, candidate)) {
                this.lastFrameScanRejectedBoundTokens++;
                continue;
            }
            TekVillageStructure structure = this.createStructure(
                    level,
                    candidate.getType(),
                    candidate.getDoorInside(),
                    candidate.getSignFacing()
            );
            if (!structure.isValid()) {
                this.clearInvalidFrameAssignment(candidate);
                this.updateFrameTokenState(candidate, null, false);
                continue;
            }
            TekVillage village = this.resolveVillageFor(level, structure, candidate);
            if (structure.getType() != TekStructureType.TOWNHALL && village == null) {
                this.clearInvalidFrameAssignment(candidate);
                this.updateFrameTokenState(candidate, null, false);
                continue;
            }
            this.cacheStructure(structure);
            this.frameAssignments.put(candidate.getFrameId(), structure.getType());
            this.updateFrameTokenState(candidate, village, true);
            this.lastFrameScanAccepted++;
        }
        return this.lastFrameScanAccepted;
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

    public int getLastFrameScanCandidates() {
        return this.lastFrameScanCandidates;
    }

    public int getLastFrameScanAccepted() {
        return this.lastFrameScanAccepted;
    }

    public int getLastFrameScanRejectedBoundTokens() {
        return this.lastFrameScanRejectedBoundTokens;
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

    private void clearInvalidFrameAssignment(TekStructureDiscovery.DiscoveredStructure candidate) {
        if (this.frameAssignments.get(candidate.getFrameId()) == candidate.getType()) {
            this.frameAssignments.remove(candidate.getFrameId());
            this.structures.remove(candidate.getType());
            this.structureCache.remove(candidate.getType());
        }
    }

    private TekVillage resolveVillageFor(ServerWorld level, TekVillageStructure structure, TekStructureDiscovery.DiscoveredStructure candidate) {
        TekVillageManager villageManager = TekVillageRuntime.get().villageManagerFor(level);
        if (structure.getType() == TekStructureType.TOWNHALL) {
            int dynamicRadius = Math.max(32, (int) Math.ceil(Math.sqrt(Math.max(1, structure.getFloorTileCount())) * 4.0D));
            return villageManager.upsertNearestVillage(structure.getDoorInside(), dynamicRadius, level.getGameTime());
        }

        TekVillage village = villageManager.findNearestVillage(structure.getDoorInside()).orElse(null);
        if (village != null && (village.contains(structure.getDoorInside()) || village.contains(candidate.getFramePos()))) {
            return village;
        }
        return null;
    }

    private void updateFrameTokenState(TekStructureDiscovery.DiscoveredStructure candidate, TekVillage village, boolean validated) {
        if (candidate.getFrame() == null || !candidate.getFrame().isAlive()) {
            return;
        }

        ItemStack current = candidate.getFrame().getItem();
        if (current.isEmpty()) {
            return;
        }

        ItemStack updated = current.copy();
        if (village != null) {
            TekItemMeta.bindToVillage(updated, village);
        }
        if (validated) {
            TekItemMeta.markStructureTokenValidated(updated);
        } else {
            TekItemMeta.clearStructureTokenValidated(updated);
        }

        if (current.getCount() != updated.getCount()
                || !ItemStack.isSame(current, updated)
                || !ItemStack.tagMatches(current, updated)) {
            candidate.getFrame().setItem(updated);
        }
    }

    private boolean canUseFrameToken(ServerWorld level, TekStructureDiscovery.DiscoveredStructure candidate) {
        UUID boundVillageId = TekItemMeta.getBoundVillageId(candidate.getMarkerItem());
        if (boundVillageId == null) {
            return true;
        }
        TekVillage village = TekVillageRuntime.get().villageManagerFor(level).findVillage(boundVillageId).orElse(null);
        return village != null
                && (village.contains(candidate.getDoorInside()) || village.contains(candidate.getFramePos()));
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
