package net.tangotek.tektopia.network.message;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.network.TekClientSyncCache;
import net.tangotek.tektopia.structures.TekVillageStructure;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageEconomy;
import net.tangotek.tektopia.village.TekVillageStructureManager;

public class PacketVillage {
    private static final int MAX_SUMMARY_LEN = 4096;
    private static final int MAX_ID_LEN = 64;

    private final String villageId;
    private final BlockPos center;
    private final int radius;
    private final int residents;
    private final int hostiles;
    private final int reservations;
    private final int invalidStructures;
    private final boolean raidActive;
    private final int raidLevel;
    private final boolean alertActive;
    private final String professions;
    private final String structures;
    private final String alert;
    private final long serverTime;

    public PacketVillage(
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
        this.professions = trim(professions, MAX_SUMMARY_LEN);
        this.structures = trim(structures, MAX_SUMMARY_LEN);
        this.alert = trim(alert, MAX_SUMMARY_LEN);
        this.serverTime = serverTime;
    }

    public static PacketVillage from(TekVillage village, TekVillageStructureManager structureManager, long serverTime) {
        Collection<TekVillageStructure> knownStructures = structureManager == null
                ? java.util.Collections.emptyList()
                : structureManager.getStructures();
        int invalid = 0;
        StringBuilder structureSummary = new StringBuilder();
        for (TekVillageStructure structure : knownStructures) {
            if (!structure.isValid()) {
                invalid++;
            }
            if (structureSummary.length() > 0) {
                structureSummary.append(", ");
            }
            structureSummary.append(structure.getType().name().toLowerCase(java.util.Locale.ROOT))
                    .append('=')
                    .append(structure.isValid() ? "valid" : structure.getValidationSummary())
                    .append("@")
                    .append(formatPos(structure.getDoorInside()));
        }
        String alert = village.getLastAlertPos() == null
                ? ""
                : formatPos(village.getLastAlertPos()) + " age=" + Math.max(0L, serverTime - village.getLastAlertTime());
        return new PacketVillage(
                village.getId().toString(),
                village.getCenter(),
                village.getRadius(),
                village.getResidents().size(),
                village.getLastKnownHostileCount(),
                TekVillageEconomy.getActiveReservationCount(),
                invalid,
                village.isRaidActive(),
                village.getRaidLevel(),
                village.hasActiveAlert(serverTime, 200L),
                formatMap(village.getProfessionCounts())
                        + " tokenPurchases=" + village.getTokenPurchaseCount()
                        + " structureCost=" + village.getStructureTokenCost()
                        + " professionCost=" + village.getProfessionTokenCost()
                        + " sales=" + String.join("|", village.getMerchantSaleHistory()),
                structureSummary.toString(),
                alert,
                serverTime
        );
    }

    public static void encode(PacketVillage msg, PacketBuffer buf) {
        buf.writeUtf(msg.villageId, MAX_ID_LEN);
        buf.writeBlockPos(msg.center);
        buf.writeInt(msg.radius);
        buf.writeInt(msg.residents);
        buf.writeInt(msg.hostiles);
        buf.writeInt(msg.reservations);
        buf.writeInt(msg.invalidStructures);
        buf.writeBoolean(msg.raidActive);
        buf.writeInt(msg.raidLevel);
        buf.writeBoolean(msg.alertActive);
        buf.writeUtf(msg.professions, MAX_SUMMARY_LEN);
        buf.writeUtf(msg.structures, MAX_SUMMARY_LEN);
        buf.writeUtf(msg.alert, MAX_SUMMARY_LEN);
        buf.writeLong(msg.serverTime);
    }

    public static PacketVillage decode(PacketBuffer buf) {
        return new PacketVillage(
                buf.readUtf(MAX_ID_LEN),
                buf.readBlockPos(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readBoolean(),
                buf.readInt(),
                buf.readBoolean(),
                buf.readUtf(MAX_SUMMARY_LEN),
                buf.readUtf(MAX_SUMMARY_LEN),
                buf.readUtf(MAX_SUMMARY_LEN),
                buf.readLong()
        );
    }

    public static void handle(PacketVillage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TekClientSyncCache.updateVillage(new TekClientSyncCache.VillageState(
                    msg.villageId,
                    msg.center,
                    msg.radius,
                    msg.residents,
                    msg.hostiles,
                    msg.reservations,
                    msg.invalidStructures,
                    msg.raidActive,
                    msg.raidLevel,
                    msg.alertActive,
                    msg.professions,
                    msg.structures,
                    msg.alert,
                    msg.serverTime
            ));
        });
        ctx.get().setPacketHandled(true);
    }

    private static String formatMap(Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return sb.toString();
    }

    private static String formatPos(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private static String trim(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
