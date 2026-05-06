package net.tangotek.tektopia.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.TekVillagerEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TekVillagerGuiSnapshotReport {
    private TekVillagerGuiSnapshotReport() {
    }

    public static List<String> format(TekVillagerEntity villager) {
        List<String> lines = new ArrayList<>();
        lines.add("Villager GUI snapshot entityId=" + villager.getId()
                + " uuid=" + shortId(villager.getUUID())
                + " health=" + String.format("%.1f", villager.getHealth()) + "/" + String.format("%.1f", villager.getMaxHealth())
                + " profession=" + villager.getProfessionType().getSerializedName()
                + " status=" + villager.getWorkerStatus().getSerializedName());
        lines.add("Stats hunger=" + villager.getHunger()
                + " happiness=" + villager.getHappy()
                + " intelligence=" + villager.getIntelligence()
                + " daysAlive=" + villager.getDaysAlive()
                + " workTime=" + villager.isWorkTime()
                + " shouldSleep=" + villager.shouldSleep()
                + " sleeping=" + villager.isSleepingState()
                + " sitting=" + villager.isSittingState());
        lines.add("Home home=" + formatPos(villager.getHomePos())
                + " bed=" + formatPos(villager.getBedPos())
                + " thought=" + blankAsDash(villager.getThoughtKey())
                + " itemThought=" + blankAsDash(villager.getItemThoughtId()));
        lines.add("Skills " + formatSkills(villager));
        lines.add("Inventory " + formatInventory(villager.getVillagerInventorySnapshot()));
        lines.add("AI filters " + formatFilters(villager));
        return lines;
    }

    private static String formatSkills(TekVillagerEntity villager) {
        List<String> skills = new ArrayList<>();
        for (ProfessionType profession : ProfessionType.values()) {
            int value = villager.getSkill(profession);
            if (value > 0) {
                skills.add(profession.getSerializedName() + "=" + value);
            }
        }
        skills.sort(Comparator.comparingInt((String entry) -> Integer.parseInt(entry.substring(entry.indexOf('=') + 1))).reversed());
        return skills.isEmpty() ? "-" : String.join(", ", skills);
    }

    private static String formatInventory(List<ItemStack> inventory) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        int occupiedSlots = 0;
        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) {
                continue;
            }
            occupiedSlots++;
            Item item = stack.getItem();
            ResourceLocation id = item.getRegistryName();
            String key = id == null ? item.toString() : id.toString();
            counts.merge(key, stack.getCount(), Integer::sum);
        }
        if (counts.isEmpty()) {
            return "slots=0/" + inventory.size() + " items=-";
        }
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            parts.add(entry.getKey() + "=" + entry.getValue());
        }
        Collections.sort(parts);
        return "slots=" + occupiedSlots + "/" + inventory.size() + " items=" + String.join(", ", parts);
    }

    private static String formatFilters(TekVillagerEntity villager) {
        List<String> filters = villager.getAIFilters();
        if (filters.isEmpty()) {
            return "-";
        }
        Collections.sort(filters);
        List<String> parts = new ArrayList<>();
        for (String filter : filters) {
            parts.add(filter + "=" + villager.isAIFilterEnabled(filter));
        }
        return String.join(", ", parts);
    }

    private static String formatPos(BlockPos pos) {
        return pos == null ? "-" : pos.toShortString();
    }

    private static String blankAsDash(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }

    private static String shortId(java.util.UUID uuid) {
        return uuid.toString().substring(0, 8);
    }
}
