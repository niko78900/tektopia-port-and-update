package net.tangotek.tektopia.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.structures.TekStructureStorage;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.structures.TekVillageStructure;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageEconomy;
import net.tangotek.tektopia.village.TekVillageManager;
import net.tangotek.tektopia.village.TekVillageStructureManager;

public final class TekStorageGuiSnapshotReport {
    private TekStorageGuiSnapshotReport() {
    }

    public static List<String> format(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager) {
        List<String> lines = new ArrayList<>();
        if (level == null || village == null) {
            lines.add("Storage snapshot unavailable.");
            return lines;
        }
        TekStructureStorage storage = resolveStorage(structureManager);
        if (storage == null || !storage.isValid()) {
            lines.add("Storage missing or invalid.");
            lines.add("Worker demand cannot be satisfied until a valid Storage is discovered.");
            return lines;
        }
        TekVillageEconomy economy = TekVillageEconomy.fromStorage(level, storage);
        lines.add("Storage chests=" + economy.getChests().size()
                + " safe=" + (storage.getSafeSpot() == null ? "-" : storage.getSafeSpot().toShortString())
                + " reservations=" + TekVillageEconomy.getActiveReservationCount(level.getGameTime()));
        lines.add("Materials iron=" + economy.countItem(Items.IRON_INGOT)
                + " gold=" + economy.countItem(Items.GOLD_INGOT)
                + " diamond=" + economy.countItem(Items.DIAMOND)
                + " emerald=" + economy.countItem(Items.EMERALD));
        lines.add("Food wheat=" + economy.countItem(Items.WHEAT)
                + " bread=" + economy.countItem(Items.BREAD)
                + " potato=" + economy.countItem(Items.POTATO)
                + " carrot=" + economy.countItem(Items.CARROT)
                + " cooked=" + cookedMeat(economy));
        lines.add("Worker stock logs=" + logs(economy)
                + " cobble=" + economy.countItem(Items.COBBLESTONE)
                + " wool=" + economy.countItem(Items.WHITE_WOOL)
                + " leather=" + economy.countItem(Items.LEATHER));
        lines.add("Reserved " + formatItems(economy.countReservedItems(level.getGameTime())));

        List<String> reservations = TekVillageEconomy.describeReservations(level.getGameTime(), 4);
        if (reservations.isEmpty()) {
            lines.add("Active reservations -");
        } else {
            for (String reservation : reservations) {
                lines.add("Reservation " + reservation);
            }
        }

        List<TekVillagerEntity> workers = level.getEntitiesOfClass(
                TekVillagerEntity.class,
                village.getBounds().inflate(16.0D),
                Entity::isAlive
        );
        workers.sort(Comparator.comparing(worker -> worker.getProfessionType().getSerializedName()));
        int missingLines = 0;
        for (TekVillagerEntity worker : workers) {
            CompoundNBT data = worker.getPersistentData();
            String result = data.getString(TekVillageManager.WORKER_LAST_RESULT_TAG);
            if (result == null || result.isEmpty() || !result.contains("missing")) {
                continue;
            }
            lines.add("Missing input " + worker.getProfessionType().getSerializedName() + "=" + result);
            missingLines++;
            if (missingLines >= 5) {
                break;
            }
        }
        if (missingLines == 0) {
            lines.add("Missing inputs -");
        }
        return lines;
    }

    private static TekStructureStorage resolveStorage(TekVillageStructureManager structureManager) {
        if (structureManager == null) {
            return null;
        }
        TekVillageStructure structure = structureManager.getStructure(TekStructureType.STORAGE).orElse(null);
        return structure instanceof TekStructureStorage ? (TekStructureStorage) structure : null;
    }

    private static int logs(TekVillageEconomy economy) {
        return economy.countItem(Items.OAK_LOG)
                + economy.countItem(Items.SPRUCE_LOG)
                + economy.countItem(Items.BIRCH_LOG)
                + economy.countItem(Items.JUNGLE_LOG)
                + economy.countItem(Items.ACACIA_LOG)
                + economy.countItem(Items.DARK_OAK_LOG);
    }

    private static int cookedMeat(TekVillageEconomy economy) {
        return economy.countItem(Items.COOKED_BEEF)
                + economy.countItem(Items.COOKED_PORKCHOP)
                + economy.countItem(Items.COOKED_CHICKEN)
                + economy.countItem(Items.COOKED_MUTTON);
    }

    private static String formatItems(Map<Item, Integer> items) {
        if (items == null || items.isEmpty()) {
            return "-";
        }
        List<String> parts = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            ResourceLocation id = entry.getKey().getRegistryName();
            parts.add((id == null ? entry.getKey().getDescriptionId() : id.toString()) + "x" + entry.getValue());
        }
        java.util.Collections.sort(parts);
        return String.join(", ", parts);
    }
}
