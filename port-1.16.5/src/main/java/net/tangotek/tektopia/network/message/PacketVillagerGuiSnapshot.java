package net.tangotek.tektopia.network.message;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.network.TekClientSyncCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PacketVillagerGuiSnapshot {
    private static final int MAX_LINES = 16;
    private static final int MAX_LINE_LEN = 512;
    private static final int MAX_INVENTORY_SLOTS = 27;
    private static final int MAX_FILTERS = 96;
    private static final int MAX_FILTER_NAME_LEN = 64;

    private final int entityId;
    private final List<String> lines;
    private final long serverTime;
    private final List<ItemStack> inventory;
    private final Map<String, Boolean> filters;

    public PacketVillagerGuiSnapshot(int entityId, List<String> lines, long serverTime) {
        this(entityId, lines, serverTime, Collections.emptyList(), Collections.emptyMap());
    }

    public PacketVillagerGuiSnapshot(
            int entityId,
            List<String> lines,
            long serverTime,
            List<ItemStack> inventory,
            Map<String, Boolean> filters
    ) {
        this.entityId = entityId;
        this.lines = sanitize(lines);
        this.serverTime = serverTime;
        this.inventory = sanitizeInventory(inventory);
        this.filters = sanitizeFilters(filters);
    }

    public static PacketVillagerGuiSnapshot from(TekVillagerEntity villager, List<String> lines, long serverTime) {
        Map<String, Boolean> filters = new LinkedHashMap<>();
        List<String> names = villager.getAIFilters();
        Collections.sort(names);
        for (String name : names) {
            filters.put(name, villager.isAIFilterEnabled(name));
        }
        return new PacketVillagerGuiSnapshot(
                villager.getId(),
                lines,
                serverTime,
                villager.getVillagerInventorySnapshot(),
                filters
        );
    }

    public static void encode(PacketVillagerGuiSnapshot msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeLong(msg.serverTime);
        buf.writeInt(msg.lines.size());
        for (String line : msg.lines) {
            buf.writeUtf(line, MAX_LINE_LEN);
        }
        buf.writeInt(msg.inventory.size());
        for (ItemStack stack : msg.inventory) {
            buf.writeItem(stack);
        }
        buf.writeInt(msg.filters.size());
        for (Map.Entry<String, Boolean> entry : msg.filters.entrySet()) {
            buf.writeUtf(entry.getKey(), MAX_FILTER_NAME_LEN);
            buf.writeBoolean(entry.getValue());
        }
    }

    public static PacketVillagerGuiSnapshot decode(PacketBuffer buf) {
        int entityId = buf.readInt();
        long serverTime = buf.readLong();
        int count = readBoundedCount(buf, MAX_LINES, "villager_gui_lines");
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            lines.add(buf.readUtf(MAX_LINE_LEN));
        }
        int inventoryCount = readBoundedCount(buf, MAX_INVENTORY_SLOTS, "villager_gui_inventory");
        List<ItemStack> inventory = new ArrayList<>();
        for (int i = 0; i < inventoryCount; i++) {
            inventory.add(buf.readItem());
        }
        int filterCount = readBoundedCount(buf, MAX_FILTERS, "villager_gui_filters");
        Map<String, Boolean> filters = new LinkedHashMap<>();
        for (int i = 0; i < filterCount; i++) {
            filters.put(buf.readUtf(MAX_FILTER_NAME_LEN), buf.readBoolean());
        }
        return new PacketVillagerGuiSnapshot(entityId, lines, serverTime, inventory, filters);
    }

    public static void handle(PacketVillagerGuiSnapshot msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TekClientSyncCache.updateGuiSnapshot(msg.entityId, msg.lines, msg.serverTime, msg.inventory, msg.filters);
        });
        ctx.get().setPacketHandled(true);
    }

    private static List<String> sanitize(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        int limit = Math.min(MAX_LINES, lines.size());
        for (int i = 0; i < limit; i++) {
            String line = lines.get(i);
            if (line == null) {
                line = "";
            }
            result.add(line.length() <= MAX_LINE_LEN ? line : line.substring(0, MAX_LINE_LEN));
        }
        return Collections.unmodifiableList(result);
    }

    private static List<ItemStack> sanitizeInventory(List<ItemStack> inventory) {
        if (inventory == null || inventory.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemStack> result = new ArrayList<>();
        int limit = Math.min(MAX_INVENTORY_SLOTS, inventory.size());
        for (int i = 0; i < limit; i++) {
            ItemStack stack = inventory.get(i);
            result.add(stack == null ? ItemStack.EMPTY : stack.copy());
        }
        return Collections.unmodifiableList(result);
    }

    private static Map<String, Boolean> sanitizeFilters(Map<String, Boolean> filters) {
        if (filters == null || filters.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Boolean> result = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : filters.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isEmpty()) {
                continue;
            }
            String key = entry.getKey().length() <= MAX_FILTER_NAME_LEN
                    ? entry.getKey()
                    : entry.getKey().substring(0, MAX_FILTER_NAME_LEN);
            result.put(key, Boolean.TRUE.equals(entry.getValue()));
            if (result.size() >= MAX_FILTERS) {
                break;
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static int readBoundedCount(PacketBuffer buf, int max, String field) {
        int count = buf.readInt();
        if (count < 0 || count > max) {
            throw new IllegalArgumentException("Invalid " + field + " count " + count + " (max " + max + ")");
        }
        return count;
    }
}
