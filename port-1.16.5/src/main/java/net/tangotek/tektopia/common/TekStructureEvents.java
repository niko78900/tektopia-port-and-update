package net.tangotek.tektopia.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.network.TekNetwork;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.structures.TekVillageStructure;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageRuntime;
import net.tangotek.tektopia.village.TekVillageManager;
import net.tangotek.tektopia.village.TekVillageStructureManager;

public class TekStructureEvents {
    private static final int DISCOVERY_RADIUS = 64;
    private static final long COMBAT_TICK_INTERVAL = 20L;
    private static final long SYNC_TICK_INTERVAL = 100L;
    private static final long DISCOVERY_TICK_INTERVAL = 200L;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isClientSide() || !(event.world instanceof ServerWorld)) {
            return;
        }
        ServerWorld level = (ServerWorld) event.world;
        TekVillageRuntime runtime = TekVillageRuntime.get();
        TekVillageManager villageManager = runtime.villageManagerFor(level);
        TekVillageStructureManager manager = runtime.managerFor(level);
        if (level.getGameTime() % COMBAT_TICK_INTERVAL == 0L) {
            villageManager.tick(level, manager);
            runtime.saveRuntime(level);
        }

        if (level.getGameTime() % SYNC_TICK_INTERVAL == 0L) {
            syncRuntimeToNearbyClients(level, villageManager, manager);
        }

        if (level.getGameTime() % DISCOVERY_TICK_INTERVAL == 0L) {
            int totalDiscovered = 0;
            for (PlayerEntity player : level.players()) {
                if (!(player instanceof ServerPlayerEntity)) {
                    continue;
                }
                totalDiscovered += manager.scanStructuresFromFrames(level, player.blockPosition(), DISCOVERY_RADIUS);
            }
            if (totalDiscovered > 0) {
                TekTopiaPort.LOGGER.debug("Structure discovery tick found {} frame markers in {}", totalDiscovered, level.dimension().location());
            }

            TekVillageStructure townHall = manager.getStructure(TekStructureType.TOWNHALL).orElse(null);
            if (townHall != null) {
                int dynamicRadius = Math.max(32, (int) Math.ceil(Math.sqrt(Math.max(1, townHall.getFloorTileCount())) * 4.0D));
                villageManager.upsertNearestVillage(townHall.getDoorInside(), dynamicRadius, level.getGameTime());
                runtime.saveRuntime(level);
                syncRuntimeToNearbyClients(level, villageManager, manager);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer().level.isClientSide() || !(event.getPlayer() instanceof ServerPlayerEntity)) {
            return;
        }
        this.syncRuntimeToNearbyClients((ServerWorld) event.getPlayer().level);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getPlayer().level.isClientSide() || !(event.getPlayer() instanceof ServerPlayerEntity)) {
            return;
        }
        this.syncRuntimeToNearbyClients((ServerWorld) event.getPlayer().level);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!(event.getWorld() instanceof ServerWorld)) {
            return;
        }
        ServerWorld level = (ServerWorld) event.getWorld();
        TekVillageRuntime runtime = TekVillageRuntime.get();
        runtime.saveRuntime(level);
        runtime.clear(level.dimension());
    }

    private void syncRuntimeToNearbyClients(ServerWorld level) {
        TekVillageRuntime runtime = TekVillageRuntime.get();
        syncRuntimeToNearbyClients(level, runtime.villageManagerFor(level), runtime.managerFor(level));
    }

    private static void syncRuntimeToNearbyClients(ServerWorld level, TekVillageManager villageManager, TekVillageStructureManager structureManager) {
        java.util.Set<Integer> syncedVillagers = new java.util.HashSet<>();
        for (TekVillage village : villageManager.getVillages()) {
            TekNetwork.sendVillageState(level, village, structureManager);
            for (TekVillagerEntity villager : level.getEntitiesOfClass(
                    TekVillagerEntity.class,
                    village.getBounds().inflate(16.0D),
                    TekVillagerEntity::isAlive
            )) {
                if (syncedVillagers.add(villager.getId())) {
                    TekNetwork.sendVillagerState(level, villager);
                }
            }
        }
    }
}
