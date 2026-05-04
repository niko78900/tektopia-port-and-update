package net.tangotek.tektopia.village;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.common.TekItemMeta;
import net.tangotek.tektopia.structures.TekStructureStorage;

public final class TekVillageEconomy {
    private static final long RESERVATION_TTL_TICKS = 1200L;
    private static final Map<String, List<ItemReservation>> RESERVATIONS_BY_STORAGE = new HashMap<>();

    public enum ArmorClass {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }

    private final List<ChestTileEntity> chests;
    private final String storageKey;

    public TekVillageEconomy(List<ChestTileEntity> chests) {
        this(chests, "anonymous");
    }

    public TekVillageEconomy(List<ChestTileEntity> chests, String storageKey) {
        this.chests = Collections.unmodifiableList(new ArrayList<>(chests));
        this.storageKey = storageKey == null || storageKey.isEmpty() ? "anonymous" : storageKey;
    }

    public List<ChestTileEntity> getChests() {
        return this.chests;
    }

    public static TekVillageEconomy fromStorage(ServerWorld level, TekStructureStorage storage) {
        List<ChestTileEntity> chests = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        for (BlockPos pos : storage.getChestPositions()) {
            keys.add(pos.asLong() + "");
            TileEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChestTileEntity) {
                chests.add((ChestTileEntity) blockEntity);
            }
        }
        Collections.sort(keys);
        String storageKey = level.dimension().location() + ":" + String.join(",", keys);
        return new TekVillageEconomy(chests, storageKey);
    }

    public int countItem(Item item) {
        int total = 0;
        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack stack = chest.getItem(slot);
                if (!stack.isEmpty() && stack.getItem() == item) {
                    total += stack.getCount();
                }
            }
        }
        return total;
    }

    public int countAvailableItem(Item item, long gameTime) {
        this.purgeExpiredReservations(gameTime);
        return Math.max(0, this.countItem(item) - this.countReservedItem(item, gameTime));
    }

    public int countReservedItem(Item item, long gameTime) {
        this.purgeExpiredReservations(gameTime);
        int total = 0;
        for (ItemReservation reservation : this.reservations()) {
            total += reservation.getReservedCount(item);
        }
        return total;
    }

    public Map<Item, Integer> countReservedItems(long gameTime) {
        this.purgeExpiredReservations(gameTime);
        Map<Item, Integer> counts = new HashMap<>();
        for (ItemReservation reservation : this.reservations()) {
            for (Map.Entry<Item, Integer> entry : reservation.getItems().entrySet()) {
                counts.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
        return counts;
    }

    public Map<Item, Integer> countItemsByType() {
        Map<Item, Integer> counts = new HashMap<>();
        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack stack = chest.getItem(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        return counts;
    }

    public boolean canInsert(ItemStack stack) {
        return this.collectInsertPlan(stack) != null;
    }

    public boolean insert(ItemStack stack) {
        List<InsertStep> insertPlan = this.collectInsertPlan(stack);
        if (insertPlan == null) {
            return false;
        }
        this.applyInsertPlan(insertPlan, stack);
        return true;
    }

    public boolean craftWithInputs(Map<Item, Integer> inputs, ItemStack output) {
        ItemStack result = TekItemMeta.markVillagerItem(output.copy());
        if (inputs.isEmpty()) {
            return this.insert(result);
        }

        List<InsertStep> insertPlan = this.collectInsertPlan(result);
        if (insertPlan == null) {
            return false;
        }

        ItemReservation reservation = this.reserveInputs("immediate_craft", inputs, 0L);
        if (reservation == null) {
            return false;
        }

        if (!this.consumeReservation(reservation)) {
            this.releaseReservation(reservation);
            return false;
        }
        this.applyInsertPlan(insertPlan, result);
        return true;
    }

    public ItemReservation reserveInputs(String owner, Map<Item, Integer> inputs, long gameTime) {
        if (inputs == null || inputs.isEmpty()) {
            return null;
        }
        this.purgeExpiredReservations(gameTime);
        Map<Item, Integer> normalized = new LinkedHashMap<>();
        for (Map.Entry<Item, Integer> entry : inputs.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0) {
                continue;
            }
            normalized.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        if (normalized.isEmpty()) {
            return null;
        }
        for (Map.Entry<Item, Integer> entry : normalized.entrySet()) {
            if (this.countAvailableItem(entry.getKey(), gameTime) < entry.getValue()) {
                return null;
            }
        }
        ItemReservation reservation = new ItemReservation(UUID.randomUUID(), owner, normalized, gameTime, this.storageKey);
        this.reservations().add(reservation);
        return reservation;
    }

    public boolean consumeReservation(ItemReservation reservation) {
        if (reservation == null || !this.storageKey.equals(reservation.getStorageKey())) {
            return false;
        }
        List<ItemReservation> reservations = this.reservations();
        if (!reservations.removeIf(active -> active.getId().equals(reservation.getId()))) {
            return false;
        }
        List<ConsumeStep> consumePlan = this.collectConsumePlan(reservation.getItems());
        if (consumePlan == null) {
            return false;
        }
        this.applyConsumePlan(consumePlan);
        return true;
    }

    public void releaseReservation(ItemReservation reservation) {
        if (reservation == null || !this.storageKey.equals(reservation.getStorageKey())) {
            return;
        }
        this.reservations().removeIf(active -> active.getId().equals(reservation.getId()));
    }

    public boolean craftWithReservedInputs(String owner, Map<Item, Integer> inputs, ItemStack output, long gameTime) {
        ItemStack result = TekItemMeta.markVillagerItem(output.copy());
        if (inputs == null || inputs.isEmpty()) {
            return this.insert(result);
        }
        List<InsertStep> insertPlan = this.collectInsertPlan(result);
        if (insertPlan == null) {
            return false;
        }
        ItemReservation reservation = this.reserveInputs(owner, inputs, gameTime);
        if (reservation == null) {
            return false;
        }
        if (!this.consumeReservation(reservation)) {
            this.releaseReservation(reservation);
            return false;
        }
        this.applyInsertPlan(insertPlan, result);
        return true;
    }

    public Map<ArmorClass, Integer> countArmorByClass(Map<Item, ArmorClass> classifier) {
        Map<ArmorClass, Integer> counts = new EnumMap<>(ArmorClass.class);
        for (ArmorClass armorClass : ArmorClass.values()) {
            counts.put(armorClass, 0);
        }
        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack stack = chest.getItem(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                ArmorClass armorClass = classifier.get(stack.getItem());
                if (armorClass == null) {
                    continue;
                }
                counts.put(armorClass, counts.get(armorClass) + stack.getCount());
            }
        }
        return counts;
    }

    public List<ItemStack> snapshotStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack stack = chest.getItem(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                stacks.add(stack.copy());
            }
        }
        return stacks;
    }

    public static CompoundNBT saveReservations() {
        CompoundNBT root = new CompoundNBT();
        ListNBT storages = new ListNBT();
        for (Map.Entry<String, List<ItemReservation>> entry : RESERVATIONS_BY_STORAGE.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            CompoundNBT storageTag = new CompoundNBT();
            storageTag.putString("storageKey", entry.getKey());
            ListNBT reservationList = new ListNBT();
            for (ItemReservation reservation : entry.getValue()) {
                CompoundNBT reservationTag = new CompoundNBT();
                reservationTag.putUUID("id", reservation.getId());
                reservationTag.putString("owner", reservation.getOwner());
                reservationTag.putLong("createdTime", reservation.getCreatedTime());
                ListNBT itemsTag = new ListNBT();
                for (Map.Entry<Item, Integer> itemEntry : reservation.getItems().entrySet()) {
                    ResourceLocation itemName = itemEntry.getKey().getRegistryName();
                    if (itemName == null || itemEntry.getValue() <= 0) {
                        continue;
                    }
                    CompoundNBT itemTag = new CompoundNBT();
                    itemTag.putString("item", itemName.toString());
                    itemTag.putInt("count", itemEntry.getValue());
                    itemsTag.add(itemTag);
                }
                if (!itemsTag.isEmpty()) {
                    reservationTag.put("items", itemsTag);
                    reservationList.add(reservationTag);
                }
            }
            if (!reservationList.isEmpty()) {
                storageTag.put("reservations", reservationList);
                storages.add(storageTag);
            }
        }
        root.put("storages", storages);
        return root;
    }

    public static void loadReservations(CompoundNBT root) {
        RESERVATIONS_BY_STORAGE.clear();
        if (root == null || !root.contains("storages", 9)) {
            return;
        }
        ListNBT storages = root.getList("storages", 10);
        for (int i = 0; i < storages.size(); i++) {
            CompoundNBT storageTag = storages.getCompound(i);
            String storageKey = storageTag.getString("storageKey");
            if (storageKey.isEmpty()) {
                continue;
            }
            ListNBT reservationList = storageTag.getList("reservations", 10);
            List<ItemReservation> loaded = new ArrayList<>();
            for (int r = 0; r < reservationList.size(); r++) {
                CompoundNBT reservationTag = reservationList.getCompound(r);
                if (!reservationTag.hasUUID("id")) {
                    continue;
                }
                Map<Item, Integer> items = new LinkedHashMap<>();
                ListNBT itemsTag = reservationTag.getList("items", 10);
                for (int itemIndex = 0; itemIndex < itemsTag.size(); itemIndex++) {
                    CompoundNBT itemTag = itemsTag.getCompound(itemIndex);
                    ResourceLocation itemName = ResourceLocation.tryParse(itemTag.getString("item"));
                    if (itemName == null) {
                        continue;
                    }
                    Item item = ForgeRegistries.ITEMS.getValue(itemName);
                    int count = itemTag.getInt("count");
                    if (item != null && count > 0) {
                        items.put(item, count);
                    }
                }
                if (!items.isEmpty()) {
                    loaded.add(new ItemReservation(
                            reservationTag.getUUID("id"),
                            reservationTag.getString("owner"),
                            items,
                            reservationTag.getLong("createdTime"),
                            storageKey
                    ));
                }
            }
            if (!loaded.isEmpty()) {
                RESERVATIONS_BY_STORAGE.put(storageKey, loaded);
            }
        }
    }

    public static int getActiveReservationCount() {
        int count = 0;
        for (List<ItemReservation> reservations : RESERVATIONS_BY_STORAGE.values()) {
            count += reservations.size();
        }
        return count;
    }

    public static int getActiveReservationCount(long gameTime) {
        purgeExpiredReservationsForAll(gameTime);
        return getActiveReservationCount();
    }

    public static int purgeExpiredReservationsForAll(long gameTime) {
        int removed = 0;
        List<String> emptyStorages = new ArrayList<>();
        for (Map.Entry<String, List<ItemReservation>> entry : RESERVATIONS_BY_STORAGE.entrySet()) {
            int before = entry.getValue().size();
            entry.getValue().removeIf(reservation -> isExpired(reservation, gameTime));
            removed += before - entry.getValue().size();
            if (entry.getValue().isEmpty()) {
                emptyStorages.add(entry.getKey());
            }
        }
        for (String storageKey : emptyStorages) {
            RESERVATIONS_BY_STORAGE.remove(storageKey);
        }
        return removed;
    }

    public static Map<String, Integer> getReservationCountsByStorage() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Map.Entry<String, List<ItemReservation>> entry : RESERVATIONS_BY_STORAGE.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                counts.put(entry.getKey(), entry.getValue().size());
            }
        }
        return counts;
    }

    public static Map<String, Integer> getReservationCountsByStorage(long gameTime) {
        purgeExpiredReservationsForAll(gameTime);
        return getReservationCountsByStorage();
    }

    public static List<String> describeReservations(long gameTime, int limit) {
        purgeExpiredReservationsForAll(gameTime);
        List<String> lines = new ArrayList<>();
        int remaining = Math.max(0, limit);
        for (Map.Entry<String, List<ItemReservation>> entry : RESERVATIONS_BY_STORAGE.entrySet()) {
            for (ItemReservation reservation : entry.getValue()) {
                if (remaining <= 0) {
                    lines.add("... " + (getActiveReservationCount() - limit) + " more reservations");
                    return lines;
                }
                long age = Math.max(0L, gameTime - reservation.getCreatedTime());
                lines.add("storage=" + entry.getKey()
                        + " owner=" + reservation.getOwner()
                        + " age=" + age + "/" + RESERVATION_TTL_TICKS
                        + " items=" + formatReservationItems(reservation.getItems()));
                remaining--;
            }
        }
        return lines;
    }

    public boolean extractOne(ItemStack template) {
        if (template == null || template.isEmpty()) {
            return false;
        }
        if (this.countAvailableItem(template.getItem(), 0L) <= 0) {
            return false;
        }
        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack stack = chest.getItem(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                if (!ItemStack.isSame(stack, template) || !ItemStack.tagMatches(stack, template)) {
                    continue;
                }
                stack.shrink(1);
                if (stack.isEmpty()) {
                    chest.setItem(slot, ItemStack.EMPTY);
                }
                chest.setChanged();
                return true;
            }
        }
        return false;
    }

    public ItemStack extractOne(Item item, long gameTime, boolean preferVillagerItem) {
        if (item == null || this.countAvailableItem(item, gameTime) <= 0) {
            return ItemStack.EMPTY;
        }
        if (preferVillagerItem) {
            ItemStack villagerStack = this.extractOneMatching(item, true);
            if (!villagerStack.isEmpty()) {
                return villagerStack;
            }
        }
        ItemStack anyStack = this.extractOneMatching(item, false);
        return anyStack.isEmpty() && !preferVillagerItem ? this.extractOneMatching(item, true) : anyStack;
    }

    private ItemStack extractOneMatching(Item item, boolean villagerOnly) {
        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack stack = chest.getItem(slot);
                if (stack.isEmpty() || stack.getItem() != item) {
                    continue;
                }
                if (villagerOnly != TekItemMeta.isVillagerItem(stack)) {
                    continue;
                }
                ItemStack extracted = stack.copy();
                extracted.setCount(1);
                stack.shrink(1);
                if (stack.isEmpty()) {
                    chest.setItem(slot, ItemStack.EMPTY);
                }
                chest.setChanged();
                return extracted;
            }
        }
        return ItemStack.EMPTY;
    }

    private List<ItemReservation> reservations() {
        return RESERVATIONS_BY_STORAGE.computeIfAbsent(this.storageKey, key -> new ArrayList<>());
    }

    private void purgeExpiredReservations(long gameTime) {
        List<ItemReservation> reservations = this.reservations();
        reservations.removeIf(reservation -> isExpired(reservation, gameTime));
        if (reservations.isEmpty()) {
            RESERVATIONS_BY_STORAGE.remove(this.storageKey);
        }
    }

    private static boolean isExpired(ItemReservation reservation, long gameTime) {
        return gameTime >= reservation.getCreatedTime()
                && gameTime - reservation.getCreatedTime() > RESERVATION_TTL_TICKS;
    }

    private static String formatReservationItems(Map<Item, Integer> items) {
        List<String> parts = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            ResourceLocation itemName = entry.getKey().getRegistryName();
            parts.add((itemName == null ? entry.getKey().getDescriptionId() : itemName.toString()) + "x" + entry.getValue());
        }
        return parts.toString();
    }

    private List<ConsumeStep> collectConsumePlan(Map<Item, Integer> inputs) {
        List<ConsumeStep> plan = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : inputs.entrySet()) {
            int remaining = Math.max(0, entry.getValue());
            if (remaining == 0) {
                continue;
            }
            Item targetItem = entry.getKey();
            for (ChestTileEntity chest : this.chests) {
                for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                    ItemStack stack = chest.getItem(slot);
                    if (stack.isEmpty() || stack.getItem() != targetItem) {
                        continue;
                    }
                    int take = Math.min(stack.getCount(), remaining);
                    if (take <= 0) {
                        continue;
                    }
                    plan.add(new ConsumeStep(chest, slot, take));
                    remaining -= take;
                    if (remaining <= 0) {
                        break;
                    }
                }
                if (remaining <= 0) {
                    break;
                }
            }
            if (remaining > 0) {
                return null;
            }
        }
        return plan;
    }

    private List<InsertStep> collectInsertPlan(ItemStack stack) {
        int remaining = stack.getCount();
        List<InsertStep> plan = new ArrayList<>();
        if (remaining <= 0) {
            return plan;
        }

        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack existing = chest.getItem(slot);
                if (existing.isEmpty() || !ItemStack.isSame(existing, stack)) {
                    continue;
                }
                int max = Math.min(existing.getMaxStackSize(), chest.getMaxStackSize());
                int room = max - existing.getCount();
                int move = Math.min(room, remaining);
                if (move <= 0) {
                    continue;
                }
                plan.add(new InsertStep(chest, slot, move, false));
                remaining -= move;
                if (remaining <= 0) {
                    return plan;
                }
            }
        }

        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack existing = chest.getItem(slot);
                if (!existing.isEmpty()) {
                    continue;
                }
                int move = Math.min(chest.getMaxStackSize(), remaining);
                plan.add(new InsertStep(chest, slot, move, true));
                remaining -= move;
                if (remaining <= 0) {
                    return plan;
                }
            }
        }
        return null;
    }

    private void applyConsumePlan(List<ConsumeStep> plan) {
        for (ConsumeStep step : plan) {
            ItemStack stack = step.chest.getItem(step.slot);
            if (stack.isEmpty()) {
                continue;
            }
            stack.shrink(step.amount);
            if (stack.isEmpty()) {
                step.chest.setItem(step.slot, ItemStack.EMPTY);
            }
            step.chest.setChanged();
        }
    }

    private void applyInsertPlan(List<InsertStep> plan, ItemStack source) {
        for (InsertStep step : plan) {
            ItemStack current = step.chest.getItem(step.slot);
            if (step.emptySlot) {
                ItemStack inserted = source.copy();
                inserted.setCount(step.amount);
                step.chest.setItem(step.slot, inserted);
            } else {
                if (!current.isEmpty()) {
                    current.grow(step.amount);
                }
            }
            step.chest.setChanged();
        }
    }

    private static final class ConsumeStep {
        private final ChestTileEntity chest;
        private final int slot;
        private final int amount;

        private ConsumeStep(ChestTileEntity chest, int slot, int amount) {
            this.chest = chest;
            this.slot = slot;
            this.amount = amount;
        }
    }

    private static final class InsertStep {
        private final ChestTileEntity chest;
        private final int slot;
        private final int amount;
        private final boolean emptySlot;

        private InsertStep(ChestTileEntity chest, int slot, int amount, boolean emptySlot) {
            this.chest = chest;
            this.slot = slot;
            this.amount = amount;
            this.emptySlot = emptySlot;
        }
    }

    public static final class ItemReservation {
        private final UUID id;
        private final String owner;
        private final Map<Item, Integer> items;
        private final long createdTime;
        private final String storageKey;

        private ItemReservation(UUID id, String owner, Map<Item, Integer> items, long createdTime, String storageKey) {
            this.id = id;
            this.owner = owner == null || owner.isEmpty() ? "unknown" : owner;
            this.items = Collections.unmodifiableMap(new LinkedHashMap<>(items));
            this.createdTime = createdTime;
            this.storageKey = storageKey;
        }

        public UUID getId() {
            return this.id;
        }

        public String getOwner() {
            return this.owner;
        }

        public Map<Item, Integer> getItems() {
            return this.items;
        }

        public long getCreatedTime() {
            return this.createdTime;
        }

        public String getStorageKey() {
            return this.storageKey;
        }

        public int getReservedCount(Item item) {
            return this.items.getOrDefault(item, 0);
        }
    }
}
