package net.tangotek.tektopia.common;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.entities.TekArchitectEntity;
import net.tangotek.tektopia.entities.TekMerchantEntity;
import net.tangotek.tektopia.entities.TekTradesmanEntity;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageManager;

public final class TekTradeGuiSnapshotReport {
    private TekTradeGuiSnapshotReport() {
    }

    public static List<String> format(ServerPlayerEntity player, TekVillage village, ServerWorld level) {
        List<String> lines = new ArrayList<>();
        if (player == null || village == null || level == null) {
            lines.add("Trade snapshot unavailable.");
            return lines;
        }
        int emeralds = countEmeralds(player);
        lines.add("Trade village=" + shortId(village.getId().toString())
                + " emeralds=" + emeralds
                + " tier=" + village.getTokenPriceTier()
                + " purchases=" + village.getTokenPurchaseCount());
        lines.add("Architect cost=" + village.getStructureTokenCost()
                + " blocked=" + blocked(emeralds, village.getStructureTokenCost()));
        lines.add("Tradesman cost=" + village.getProfessionTokenCost()
                + " blocked=" + blocked(emeralds, village.getProfessionTokenCost()));
        lines.add("Recent merchant sales=" + (village.getMerchantSaleHistory().isEmpty()
                ? "-"
                : String.join(",", village.getMerchantSaleHistory())));

        List<TekVillagerEntity> traders = level.getEntitiesOfClass(
                TekVillagerEntity.class,
                village.getBounds().inflate(16.0D),
                Entity::isAlive
        );
        int traderLines = 0;
        for (TekVillagerEntity trader : traders) {
            if (!(trader instanceof TekArchitectEntity)
                    && !(trader instanceof TekTradesmanEntity)
                    && !(trader instanceof TekMerchantEntity)) {
                continue;
            }
            CompoundNBT data = trader.getPersistentData();
            String result = data.getString(TekVillageManager.WORKER_LAST_RESULT_TAG);
            lines.add("Trader " + trader.getProfessionType().getSerializedName()
                    + " #" + trader.getId()
                    + " status=" + trader.getWorkerStatus().getSerializedName()
                    + " last=" + blank(result));
            traderLines++;
        }
        if (traderLines == 0) {
            lines.add("Traders none in nearest village.");
        }
        lines.add("Actions use villager GUI buttons; server remains authoritative.");
        return lines;
    }

    private static int countEmeralds(ServerPlayerEntity player) {
        int total = 0;
        for (int slot = 0; slot < player.inventory.getContainerSize(); slot++) {
            ItemStack stack = player.inventory.getItem(slot);
            if (!stack.isEmpty() && stack.getItem() == Items.EMERALD) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static String blocked(int emeralds, int cost) {
        return emeralds >= cost ? "-" : "needs_" + (cost - emeralds) + "_emeralds";
    }

    private static String blank(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }

    private static String shortId(String id) {
        return id == null || id.length() <= 8 ? blank(id) : id.substring(0, 8);
    }
}
