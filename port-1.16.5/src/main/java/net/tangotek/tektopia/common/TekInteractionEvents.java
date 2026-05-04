package net.tangotek.tektopia.common;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.tangotek.tektopia.entities.TekArchitectEntity;
import net.tangotek.tektopia.entities.TekChildEntity;
import net.tangotek.tektopia.entities.TekGuardEntity;
import net.tangotek.tektopia.entities.TekTradesmanEntity;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.registry.TekEntities;
import net.tangotek.tektopia.registry.TekItems;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageManager;
import net.tangotek.tektopia.village.TekVillageRuntime;

public class TekInteractionEvents {
    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getWorld().isClientSide() || !(event.getWorld() instanceof ServerWorld)) {
            return;
        }
        Entity target = event.getTarget();
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        ItemStack held = event.getItemStack();
        if (held.isEmpty()) {
            return;
        }
        if (target instanceof TekArchitectEntity && held.getItem() == Items.EMERALD) {
            if (this.sellStructureToken(player, (ServerWorld) event.getWorld(), (TekArchitectEntity) target, held)) {
                this.finishInteraction(event);
            }
            return;
        }
        if (target instanceof TekTradesmanEntity && held.getItem() == Items.EMERALD) {
            if (this.sellProfessionToken(player, (ServerWorld) event.getWorld(), (TekTradesmanEntity) target, held)) {
                this.finishInteraction(event);
            }
            return;
        }
        if (!(target instanceof TekVillagerEntity)) {
            return;
        }
        TekVillagerEntity villager = (TekVillagerEntity) target;
        ResourceLocation itemName = held.getItem().getRegistryName();
        if (itemName == null || !"tektopia".equals(itemName.getNamespace())) {
            return;
        }
        if ("heart".equals(itemName.getPath())) {
            if (this.spawnChildFromHeart(player, (ServerWorld) event.getWorld(), villager, held)) {
                this.finishInteraction(event);
            }
            return;
        }
        if ("prof_captain".equals(itemName.getPath()) && villager instanceof TekGuardEntity) {
            ((TekGuardEntity) villager).setCaptain(true);
            villager.setHappy(100);
            this.consumeHeld(player, held, 1);
            this.finishInteraction(event);
            return;
        }
        EntityType<? extends TekVillagerEntity> replacementType = this.professionEntityForToken(itemName.getPath());
        if (replacementType == null) {
            return;
        }
        TekVillage village = this.findNearestVillage((ServerWorld) event.getWorld(), villager).orElse(null);
        if (!TekItemMeta.canUseInVillage(held, village)) {
            player.sendMessage(new net.minecraft.util.text.StringTextComponent("That TekTopia token belongs to another village."), player.getUUID());
            this.finishInteraction(event);
            return;
        }
        TekVillagerEntity replacement = replacementType.create((ServerWorld) event.getWorld());
        if (replacement == null) {
            return;
        }
        this.copyVillagerCore(villager, replacement);
        replacement.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.yRot, villager.xRot);
        ((ServerWorld) event.getWorld()).addFreshEntity(replacement);
        villager.remove();
        this.consumeHeld(player, held, 1);
        this.finishInteraction(event);
    }

    private boolean sellStructureToken(ServerPlayerEntity player, ServerWorld level, TekArchitectEntity architect, ItemStack emeralds) {
        TekVillage village = this.findNearestVillage(level, architect).orElse(null);
        if (village == null) {
            return false;
        }
        int cost = village.getStructureTokenCost();
        if (emeralds.getCount() < cost) {
            player.sendMessage(new net.minecraft.util.text.StringTextComponent("Architect needs " + cost + " emeralds."), player.getUUID());
            return true;
        }
        RegistryObject<Item>[] tokens = this.structureTokenCycle();
        RegistryObject<Item> token = tokens[Math.floorMod(village.getTokenPurchaseCount(), tokens.length)];
        ItemStack sold = TekItemMeta.bindToVillage(new ItemStack(token.get()), village);
        player.inventory.placeItemBackInInventory(level, sold);
        this.consumeHeld(player, emeralds, cost);
        village.incrementTokenPurchaseCount();
        TekVillageRuntime.get().saveRuntime(level);
        String soldName = sold.getHoverName().getString();
        player.sendMessage(new net.minecraft.util.text.StringTextComponent("Architect sold " + soldName + " for " + cost + " emeralds. Next structure token costs " + village.getStructureTokenCost() + "."), player.getUUID());
        return true;
    }

    private boolean sellProfessionToken(ServerPlayerEntity player, ServerWorld level, TekTradesmanEntity tradesman, ItemStack emeralds) {
        TekVillage village = this.findNearestVillage(level, tradesman).orElse(null);
        if (village == null) {
            return false;
        }
        int cost = village.getProfessionTokenCost();
        if (emeralds.getCount() < cost) {
            player.sendMessage(new net.minecraft.util.text.StringTextComponent("Tradesman needs " + cost + " emeralds."), player.getUUID());
            return true;
        }
        RegistryObject<Item>[] tokens = this.professionTokenCycle();
        RegistryObject<Item> token = tokens[Math.floorMod(village.getTokenPurchaseCount(), tokens.length)];
        ItemStack sold = TekItemMeta.bindToVillage(new ItemStack(token.get()), village);
        player.inventory.placeItemBackInInventory(level, sold);
        this.consumeHeld(player, emeralds, cost);
        village.incrementTokenPurchaseCount();
        TekVillageRuntime.get().saveRuntime(level);
        String soldName = sold.getHoverName().getString();
        player.sendMessage(new net.minecraft.util.text.StringTextComponent("Tradesman sold " + soldName + " for " + cost + " emeralds. Next profession token costs " + village.getProfessionTokenCost() + "."), player.getUUID());
        return true;
    }

    private boolean spawnChildFromHeart(ServerPlayerEntity player, ServerWorld level, TekVillagerEntity parent, ItemStack heart) {
        TekChildEntity child = TekEntities.TEK_CHILD.get().create(level);
        if (child == null) {
            return false;
        }
        child.moveTo(parent.getX(), parent.getY(), parent.getZ(), parent.yRot, parent.xRot);
        child.setHomePos(parent.getHomePos());
        child.setBedPos(parent.getBedPos());
        child.setHappy(100);
        child.setThoughtKey("new_child");
        level.addFreshEntity(child);
        this.consumeHeld(player, heart, 1);
        return true;
    }

    private Optional<TekVillage> findNearestVillage(ServerWorld level, Entity entity) {
        TekVillageManager manager = TekVillageRuntime.get().villageManagerFor(level);
        return manager.findNearestVillage(entity.blockPosition());
    }

    private void copyVillagerCore(TekVillagerEntity from, TekVillagerEntity to) {
        to.setHunger(from.getHunger());
        to.setHappy(from.getHappy());
        to.setIntelligence(from.getIntelligence());
        to.setDaysAlive(from.getDaysAlive());
        to.setHomePos(from.getHomePos());
        to.setBedPos(from.getBedPos());
        to.setThoughtKey("profession_changed");
        for (ItemStack stack : from.getVillagerInventorySnapshot()) {
            to.addToVillagerInventory(stack);
        }
    }

    private EntityType<? extends TekVillagerEntity> professionEntityForToken(String path) {
        switch (path) {
            case "prof_bard":
                return TekEntities.TEK_BARD.get();
            case "prof_blacksmith":
                return TekEntities.TEK_BLACKSMITH.get();
            case "prof_butcher":
                return TekEntities.TEK_BUTCHER.get();
            case "prof_chef":
                return TekEntities.TEK_CHEF.get();
            case "prof_cleric":
                return TekEntities.TEK_CLERIC.get();
            case "prof_druid":
                return TekEntities.TEK_DRUID.get();
            case "prof_enchanter":
                return TekEntities.TEK_ENCHANTER.get();
            case "prof_farmer":
                return TekEntities.TEK_FARMER.get();
            case "prof_guard":
                return TekEntities.TEK_GUARD.get();
            case "prof_lumberjack":
                return TekEntities.TEK_LUMBERJACK.get();
            case "prof_miner":
                return TekEntities.TEK_MINER.get();
            case "prof_rancher":
                return TekEntities.TEK_RANCHER.get();
            case "prof_teacher":
                return TekEntities.TEK_TEACHER.get();
            case "prof_child":
                return TekEntities.TEK_CHILD.get();
            case "prof_nitwit":
                return TekEntities.TEK_NITWIT.get();
            case "prof_nomad":
                return TekEntities.TEK_NOMAD.get();
            default:
                return null;
        }
    }

    @SuppressWarnings("unchecked")
    private RegistryObject<Item>[] structureTokenCycle() {
        return new RegistryObject[] {
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
    }

    @SuppressWarnings("unchecked")
    private RegistryObject<Item>[] professionTokenCycle() {
        return new RegistryObject[] {
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
    }

    private void consumeHeld(PlayerEntity player, ItemStack stack, int count) {
        if (!player.isCreative()) {
            stack.shrink(count);
        }
    }

    private void finishInteraction(PlayerInteractEvent.EntityInteract event) {
        event.setCancellationResult(ActionResultType.SUCCESS);
        event.setCanceled(true);
    }
}
