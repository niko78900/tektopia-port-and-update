package net.tangotek.tektopia.network;

import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public final class TekClientSyncCache {
    private static final Map<String, VillageState> VILLAGES = new LinkedHashMap<>();
    private static final Map<Integer, VillagerState> VILLAGERS = new LinkedHashMap<>();
    private static final Map<Integer, PathNodeState> PATH_NODES = new LinkedHashMap<>();
    private static final Map<Integer, Map<String, Boolean>> AI_FILTERS = new LinkedHashMap<>();
    private static final Map<Integer, GuiSnapshotState> GUI_SNAPSHOTS = new LinkedHashMap<>();
    private static final Map<String, TextSnapshotState> VILLAGE_GUI_SNAPSHOTS = new LinkedHashMap<>();
    private static final Map<String, TextSnapshotState> STORAGE_GUI_SNAPSHOTS = new LinkedHashMap<>();
    private static final Map<String, TextSnapshotState> TRADE_GUI_SNAPSHOTS = new LinkedHashMap<>();

    private TekClientSyncCache() {
    }

    public static void updateVillage(VillageState state) {
        if (state != null && !state.villageId.isEmpty()) {
            VILLAGES.put(state.villageId, state);
        }
    }

    public static void updateThought(int entityId, String thoughtKey, String workerStatus, String profession) {
        VillagerState previous = VILLAGERS.get(entityId);
        String itemThought = previous == null ? "" : previous.itemThought;
        VILLAGERS.put(entityId, new VillagerState(entityId, thoughtKey, itemThought, workerStatus, profession));
    }

    public static void updateItemThought(int entityId, String itemThought) {
        VillagerState previous = VILLAGERS.get(entityId);
        String thoughtKey = previous == null ? "" : previous.thoughtKey;
        String workerStatus = previous == null ? "" : previous.workerStatus;
        String profession = previous == null ? "" : previous.profession;
        VILLAGERS.put(entityId, new VillagerState(entityId, thoughtKey, itemThought, workerStatus, profession));
    }

    public static void updatePathNode(PathNodeState state) {
        if (state == null) {
            return;
        }
        if (state.clearOnly) {
            PATH_NODES.remove(state.entityId);
        } else {
            PATH_NODES.put(state.entityId, state);
        }
    }

    public static void clearPathNodes() {
        PATH_NODES.clear();
    }

    public static void updateAIFilter(int entityId, String filterName, boolean enabled) {
        if (filterName == null || filterName.isEmpty()) {
            return;
        }
        AI_FILTERS.computeIfAbsent(entityId, ignored -> new LinkedHashMap<>()).put(filterName, enabled);
    }

    public static void updateGuiSnapshot(int entityId, List<String> lines, long serverTime) {
        updateGuiSnapshot(entityId, lines, serverTime, Collections.emptyList(), Collections.emptyMap());
    }

    public static void updateGuiSnapshot(
            int entityId,
            List<String> lines,
            long serverTime,
            List<ItemStack> inventory,
            Map<String, Boolean> filters
    ) {
        GUI_SNAPSHOTS.put(entityId, new GuiSnapshotState(entityId, lines, serverTime, inventory, filters));
        if (filters != null && !filters.isEmpty()) {
            AI_FILTERS.computeIfAbsent(entityId, ignored -> new LinkedHashMap<>()).putAll(filters);
        }
    }

    public static void updateVillageGuiSnapshot(String villageId, List<String> lines, long serverTime) {
        updateTextSnapshot(VILLAGE_GUI_SNAPSHOTS, villageId, lines, serverTime);
    }

    public static void updateStorageGuiSnapshot(String villageId, List<String> lines, long serverTime) {
        updateTextSnapshot(STORAGE_GUI_SNAPSHOTS, villageId, lines, serverTime);
    }

    public static void updateTradeGuiSnapshot(String villageId, List<String> lines, long serverTime) {
        updateTextSnapshot(TRADE_GUI_SNAPSHOTS, villageId, lines, serverTime);
    }

    public static Map<String, VillageState> getVillages() {
        return Collections.unmodifiableMap(VILLAGES);
    }

    public static Map<Integer, VillagerState> getVillagers() {
        return Collections.unmodifiableMap(VILLAGERS);
    }

    public static Map<Integer, PathNodeState> getPathNodes() {
        return Collections.unmodifiableMap(PATH_NODES);
    }

    public static Map<Integer, Map<String, Boolean>> getAIFilters() {
        return Collections.unmodifiableMap(AI_FILTERS);
    }

    public static Map<Integer, GuiSnapshotState> getGuiSnapshots() {
        return Collections.unmodifiableMap(GUI_SNAPSHOTS);
    }

    public static Map<String, TextSnapshotState> getVillageGuiSnapshots() {
        return Collections.unmodifiableMap(VILLAGE_GUI_SNAPSHOTS);
    }

    public static Map<String, TextSnapshotState> getStorageGuiSnapshots() {
        return Collections.unmodifiableMap(STORAGE_GUI_SNAPSHOTS);
    }

    public static Map<String, TextSnapshotState> getTradeGuiSnapshots() {
        return Collections.unmodifiableMap(TRADE_GUI_SNAPSHOTS);
    }

    public static TextSnapshotState latestVillageGuiSnapshot() {
        return latest(VILLAGE_GUI_SNAPSHOTS);
    }

    public static TextSnapshotState latestStorageGuiSnapshot() {
        return latest(STORAGE_GUI_SNAPSHOTS);
    }

    public static TextSnapshotState latestTradeGuiSnapshot() {
        return latest(TRADE_GUI_SNAPSHOTS);
    }

    private static void updateTextSnapshot(Map<String, TextSnapshotState> target, String villageId, List<String> lines, long serverTime) {
        String key = villageId == null || villageId.isEmpty() ? "nearest" : villageId;
        target.put(key, new TextSnapshotState(key, lines, serverTime));
    }

    private static TextSnapshotState latest(Map<String, TextSnapshotState> source) {
        TextSnapshotState latest = null;
        for (TextSnapshotState state : source.values()) {
            if (latest == null || state.serverTime >= latest.serverTime) {
                latest = state;
            }
        }
        return latest;
    }

    public static final class VillageState {
        public final String villageId;
        public final BlockPos center;
        public final int radius;
        public final int residents;
        public final int hostiles;
        public final int reservations;
        public final int invalidStructures;
        public final boolean raidActive;
        public final int raidLevel;
        public final boolean alertActive;
        public final String professions;
        public final String structures;
        public final String alert;
        public final long serverTime;

        public VillageState(
                String villageId,
                BlockPos center,
                int radius,
                int residents,
                int hostiles,
                int reservations,
                int invalidStructures,
                boolean raidActive,
                int raidLevel,
                boolean alertActive,
                String professions,
                String structures,
                String alert,
                long serverTime
        ) {
            this.villageId = villageId == null ? "" : villageId;
            this.center = center == null ? BlockPos.ZERO : center.immutable();
            this.radius = radius;
            this.residents = residents;
            this.hostiles = hostiles;
            this.reservations = reservations;
            this.invalidStructures = invalidStructures;
            this.raidActive = raidActive;
            this.raidLevel = raidLevel;
            this.alertActive = alertActive;
            this.professions = professions == null ? "" : professions;
            this.structures = structures == null ? "" : structures;
            this.alert = alert == null ? "" : alert;
            this.serverTime = serverTime;
        }
    }

    public static final class VillagerState {
        public final int entityId;
        public final String thoughtKey;
        public final String itemThought;
        public final String workerStatus;
        public final String profession;

        public VillagerState(int entityId, String thoughtKey, String itemThought, String workerStatus, String profession) {
            this.entityId = entityId;
            this.thoughtKey = thoughtKey == null ? "" : thoughtKey;
            this.itemThought = itemThought == null ? "" : itemThought;
            this.workerStatus = workerStatus == null ? "" : workerStatus;
            this.profession = profession == null ? "" : profession;
        }
    }

    public static final class PathNodeState {
        public final int entityId;
        public final BlockPos target;
        public final String reason;
        public final boolean clearOnly;

        public PathNodeState(int entityId, BlockPos target, String reason, boolean clearOnly) {
            this.entityId = entityId;
            this.target = target == null ? BlockPos.ZERO : target.immutable();
            this.reason = reason == null ? "" : reason;
            this.clearOnly = clearOnly;
        }
    }

    public static final class GuiSnapshotState {
        public final int entityId;
        public final List<String> lines;
        public final long serverTime;
        public final List<ItemStack> inventory;
        public final Map<String, Boolean> filters;

        public GuiSnapshotState(int entityId, List<String> lines, long serverTime) {
            this(entityId, lines, serverTime, Collections.emptyList(), Collections.emptyMap());
        }

        public GuiSnapshotState(
                int entityId,
                List<String> lines,
                long serverTime,
                List<ItemStack> inventory,
                Map<String, Boolean> filters
        ) {
            this.entityId = entityId;
            this.lines = Collections.unmodifiableList(new ArrayList<>(lines == null ? Collections.emptyList() : lines));
            this.serverTime = serverTime;
            List<ItemStack> stacks = new ArrayList<>();
            if (inventory != null) {
                for (ItemStack stack : inventory) {
                    stacks.add(stack == null ? ItemStack.EMPTY : stack.copy());
                }
            }
            this.inventory = Collections.unmodifiableList(stacks);
            this.filters = Collections.unmodifiableMap(new LinkedHashMap<>(filters == null ? Collections.emptyMap() : filters));
        }
    }

    public static final class TextSnapshotState {
        public final String key;
        public final List<String> lines;
        public final long serverTime;

        public TextSnapshotState(String key, List<String> lines, long serverTime) {
            this.key = key == null ? "" : key;
            this.lines = Collections.unmodifiableList(new ArrayList<>(lines == null ? Collections.emptyList() : lines));
            this.serverTime = serverTime;
        }
    }
}
