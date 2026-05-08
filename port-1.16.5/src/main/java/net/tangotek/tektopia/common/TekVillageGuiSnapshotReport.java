package net.tangotek.tektopia.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.structures.TekVillageStructure;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageEconomy;
import net.tangotek.tektopia.village.TekVillageManager;
import net.tangotek.tektopia.village.TekVillageStructureManager;

public final class TekVillageGuiSnapshotReport {
    private TekVillageGuiSnapshotReport() {
    }

    public static List<String> format(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager) {
        List<String> lines = new ArrayList<>();
        if (level == null || village == null) {
            lines.add("Village snapshot unavailable.");
            return lines;
        }
        long gameTime = level.getGameTime();
        lines.add("Village " + shortId(village.getId().toString())
                + " center=" + formatPos(village.getCenter())
                + " radius=" + village.getRadius()
                + " residents=" + village.getResidents().size()
                + " hostiles=" + village.getLastKnownHostileCount());
        lines.add("Professions " + (village.getProfessionCounts().isEmpty() ? "-" : village.getProfessionCounts().toString()));
        lines.add("Raid active=" + village.isRaidActive()
                + " level=" + village.getRaidLevel()
                + " alert=" + (village.hasActiveAlert(gameTime, 200L) ? formatPos(village.getLastAlertPos()) : "-")
                + " reservations=" + TekVillageEconomy.getActiveReservationCount(gameTime));

        List<TekVillageStructure> structures = new ArrayList<>(structureManager == null
                ? java.util.Collections.emptyList()
                : structureManager.getStructures());
        structures.sort(Comparator.comparing(structure -> structure.getType().name()));
        lines.add("Structures total=" + structures.size() + " invalid=" + structures.stream().filter(structure -> !structure.isValid()).count());
        int structureLimit = Math.min(8, structures.size());
        for (int i = 0; i < structureLimit; i++) {
            TekVillageStructure structure = structures.get(i);
            lines.add(structure.getType().getDisplayName()
                    + " valid=" + structure.isValid()
                    + " door=" + formatPos(structure.getDoorInside())
                    + " safe=" + formatPos(structure.getSafeSpot())
                    + " summary=" + structure.getValidationSummary());
        }
        if (structures.size() > structureLimit) {
            lines.add("... " + (structures.size() - structureLimit) + " more structures");
        }

        List<TekVillagerEntity> villagers = level.getEntitiesOfClass(
                TekVillagerEntity.class,
                village.getBounds().inflate(16.0D),
                Entity::isAlive
        );
        villagers.sort(Comparator.comparing(villager -> villager.getProfessionType().getSerializedName()));
        int workerLimit = Math.min(8, villagers.size());
        for (int i = 0; i < workerLimit; i++) {
            TekVillagerEntity villager = villagers.get(i);
            CompoundNBT data = villager.getPersistentData();
            String mode = data.getString(TekVillageManager.WORKER_MODE_TAG);
            String result = data.getString(TekVillageManager.WORKER_LAST_RESULT_TAG);
            lines.add("Worker " + villager.getProfessionType().getSerializedName()
                    + " #" + villager.getId()
                    + " status=" + villager.getWorkerStatus().getSerializedName()
                    + " mode=" + blank(mode)
                    + " result=" + blank(result));
        }
        if (villagers.size() > workerLimit) {
            lines.add("... " + (villagers.size() - workerLimit) + " more workers");
        }
        return lines;
    }

    private static String formatPos(BlockPos pos) {
        return pos == null ? "-" : pos.toShortString();
    }

    private static String blank(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }

    private static String shortId(String id) {
        return id == null || id.length() <= 8 ? blank(id) : id.substring(0, 8);
    }
}
