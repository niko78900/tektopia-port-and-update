package net.tangotek.tektopia.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;
import net.tangotek.tektopia.entities.TekArchitectEntity;
import net.tangotek.tektopia.entities.TekMerchantEntity;
import net.tangotek.tektopia.entities.TekTradesmanEntity;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.registry.TekItems;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageRuntime;

public final class TekTradeActions {
    public static final String ACTION_STRUCTURE_TOKEN = "structure_token";
    public static final String ACTION_PROFESSION_TOKEN = "profession_token";

    private TekTradeActions() {
    }

    public static Result purchaseFromGui(ServerPlayerEntity player, Entity target, String action) {
        if (player == null || target == null || !(target.level instanceof ServerWorld)) {
            return Result.fail("Trade target is unavailable.");
        }
        if (player.isSpectator()) {
            return Result.fail("Spectators cannot trade with TekTopia villagers.");
        }
        if (!(target instanceof TekVillagerEntity) || !TekVillagerContainer.canInteract(player, target, target.getId())) {
            return Result.fail("That entity is not a TekTopia trader.");
        }
        if (!TekVillagerContainer.isOpenFor(player, target.getId())) {
            return Result.fail("Trade rejected because the villager GUI is not open.");
        }
        ServerWorld level = (ServerWorld) target.level;
        TekVillage village = TekVillageRuntime.get().villageManagerFor(level).findNearestVillage(target.blockPosition()).orElse(null);
        if (village == null || !village.contains(target.blockPosition())) {
            return Result.fail("No TekTopia village is close enough for this trade.");
        }
        if (ACTION_STRUCTURE_TOKEN.equals(action) && target instanceof TekArchitectEntity) {
            return purchaseStructureToken(player, level, village);
        }
        if (ACTION_PROFESSION_TOKEN.equals(action) && target instanceof TekTradesmanEntity) {
            return purchaseProfessionToken(player, level, village);
        }
        if (target instanceof TekMerchantEntity) {
            return Result.fail("Merchant trades are driven by village storage; use the storage/economy status to clear blocked sale inputs.");
        }
        return Result.fail("This villager does not offer that trade.");
    }

    public static String formatTradeLine(TekVillagerEntity villager, TekVillage village) {
        if (villager == null || village == null) {
            return "";
        }
        if (villager instanceof TekArchitectEntity) {
            return "Trade architect action=" + ACTION_STRUCTURE_TOKEN
                    + " structureCost=" + village.getStructureTokenCost()
                    + " emeralds tier=" + village.getTokenPriceTier()
                    + " next=" + registryName(nextStructureToken(village).get());
        }
        if (villager instanceof TekTradesmanEntity) {
            return "Trade tradesman action=" + ACTION_PROFESSION_TOKEN
                    + " professionCost=" + village.getProfessionTokenCost()
                    + " emeralds tier=" + village.getTokenPriceTier()
                    + " next=" + registryName(nextProfessionToken(village).get());
        }
        if (villager instanceof TekMerchantEntity) {
            return "Trade merchant recentSales="
                    + (village.getMerchantSaleHistory().isEmpty() ? "-" : String.join(",", village.getMerchantSaleHistory()));
        }
        return "";
    }

    private static Result purchaseStructureToken(ServerPlayerEntity player, ServerWorld level, TekVillage village) {
        int cost = village.getStructureTokenCost();
        int emeralds = countInventoryItem(player, Items.EMERALD);
        if (emeralds < cost) {
            return Result.fail("Architect needs " + cost + " emeralds; you have " + emeralds + ".");
        }
        RegistryObject<Item> token = nextStructureToken(village);
        ItemStack sold = TekItemMeta.bindToVillage(new ItemStack(token.get()), village);
        consumeInventoryItem(player, Items.EMERALD, cost);
        player.inventory.placeItemBackInInventory(level, sold);
        village.incrementTokenPurchaseCount();
        TekVillageRuntime.get().saveRuntime(level);
        return Result.success("Architect sold " + sold.getHoverName().getString() + " for " + cost + " emeralds.");
    }

    private static Result purchaseProfessionToken(ServerPlayerEntity player, ServerWorld level, TekVillage village) {
        int cost = village.getProfessionTokenCost();
        int emeralds = countInventoryItem(player, Items.EMERALD);
        if (emeralds < cost) {
            return Result.fail("Tradesman needs " + cost + " emeralds; you have " + emeralds + ".");
        }
        RegistryObject<Item> token = nextProfessionToken(village);
        ItemStack sold = TekItemMeta.bindToVillage(new ItemStack(token.get()), village);
        consumeInventoryItem(player, Items.EMERALD, cost);
        player.inventory.placeItemBackInInventory(level, sold);
        village.incrementTokenPurchaseCount();
        TekVillageRuntime.get().saveRuntime(level);
        return Result.success("Tradesman sold " + sold.getHoverName().getString() + " for " + cost + " emeralds.");
    }

    @SuppressWarnings("unchecked")
    private static RegistryObject<Item> nextStructureToken(TekVillage village) {
        RegistryObject<Item>[] tokens = new RegistryObject[] {
                TekItems.STRUCTURE_STORAGE_TOKEN,
                TekItems.STRUCTURE_HOME2_TOKEN,
                TekItems.STRUCTURE_FARM_TOKEN,
                TekItems.STRUCTURE_GUARD_POST_TOKEN,
                TekItems.STRUCTURE_MINESHAFT_TOKEN,
                TekItems.STRUCTURE_LUMBER_TOKEN,
                TekItems.STRUCTURE_KITCHEN_TOKEN,
                TekItems.STRUCTURE_BLACKSMITH_TOKEN,
                TekItems.STRUCTURE_BUTCHER_TOKEN,
                TekItems.STRUCTURE_SHEEP_PEN_TOKEN,
                TekItems.STRUCTURE_COW_PEN_TOKEN,
                TekItems.STRUCTURE_PIG_PEN_TOKEN,
                TekItems.STRUCTURE_CHICKEN_COOP_TOKEN,
                TekItems.STRUCTURE_MERCHANT_STALL_TOKEN,
                TekItems.STRUCTURE_BARRACKS_TOKEN,
                TekItems.STRUCTURE_TAVERN_TOKEN,
                TekItems.STRUCTURE_SCHOOL_TOKEN,
                TekItems.STRUCTURE_LIBRARY_TOKEN
        };
        return tokens[Math.floorMod(village.getTokenPurchaseCount(), tokens.length)];
    }

    @SuppressWarnings("unchecked")
    private static RegistryObject<Item> nextProfessionToken(TekVillage village) {
        RegistryObject<Item>[] tokens = new RegistryObject[] {
                TekItems.PROF_FARMER,
                TekItems.PROF_LUMBERJACK,
                TekItems.PROF_MINER,
                TekItems.PROF_GUARD,
                TekItems.PROF_BLACKSMITH,
                TekItems.PROF_CHEF,
                TekItems.PROF_RANCHER,
                TekItems.PROF_BUTCHER,
                TekItems.PROF_TEACHER,
                TekItems.PROF_BARD,
                TekItems.PROF_CLERIC,
                TekItems.PROF_DRUID,
                TekItems.PROF_ENCHANTER,
                TekItems.PROF_NITWIT,
                TekItems.PROF_CAPTAIN
        };
        return tokens[Math.floorMod(village.getTokenPurchaseCount(), tokens.length)];
    }

    private static int countInventoryItem(ServerPlayerEntity player, Item item) {
        int total = 0;
        for (int slot = 0; slot < player.inventory.getContainerSize(); slot++) {
            ItemStack stack = player.inventory.getItem(slot);
            if (!stack.isEmpty() && stack.getItem() == item) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static void consumeInventoryItem(ServerPlayerEntity player, Item item, int amount) {
        if (player.isCreative() || amount <= 0) {
            return;
        }
        int remaining = amount;
        for (int slot = 0; slot < player.inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = player.inventory.getItem(slot);
            if (stack.isEmpty() || stack.getItem() != item) {
                continue;
            }
            int take = Math.min(remaining, stack.getCount());
            stack.shrink(take);
            remaining -= take;
            if (stack.isEmpty()) {
                player.inventory.setItem(slot, ItemStack.EMPTY);
            }
        }
    }

    private static String registryName(Item item) {
        return item.getRegistryName() == null ? item.getDescriptionId() : item.getRegistryName().toString();
    }

    public static final class Result {
        private final boolean success;
        private final String message;

        private Result(boolean success, String message) {
            this.success = success;
            this.message = message == null ? "" : message;
        }

        public static Result success(String message) {
            return new Result(true, message);
        }

        public static Result fail(String message) {
            return new Result(false, message);
        }

        public boolean isSuccess() {
            return this.success;
        }

        public String getMessage() {
            return this.message;
        }

        public void sendTo(ServerPlayerEntity player) {
            if (player != null && !this.message.isEmpty()) {
                player.sendMessage(new StringTextComponent(this.message), player.getUUID());
            }
        }
    }
}
