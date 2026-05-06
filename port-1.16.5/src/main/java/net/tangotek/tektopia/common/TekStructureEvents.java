package net.tangotek.tektopia.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.network.TekNetwork;
import net.tangotek.tektopia.network.message.PacketVillage;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.structures.TekVillageStructure;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageRuntime;
import net.tangotek.tektopia.village.TekVillageManager;
import net.tangotek.tektopia.village.TekVillagePerfStats;
import net.tangotek.tektopia.village.TekVillageStructureManager;

public class TekStructureEvents {
    private static final int DISCOVERY_RADIUS = 64;
    private static final long COMBAT_TICK_INTERVAL = 20L;
    private static final long DISCOVERY_TICK_INTERVAL = 200L;
    private static final long VILLAGE_SYNC_TICK_INTERVAL = 100L;

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
            long startNanos = System.nanoTime();
            villageManager.tick(level, manager);
            TekVillagePerfStats.recordVillageTick(System.nanoTime() - startNanos, villageManager.size());
            runtime.saveRuntime(level);
        }

        if (level.getGameTime() % DISCOVERY_TICK_INTERVAL == 0L) {
            int totalDiscovered = 0;
            for (PlayerEntity player : level.players()) {
                if (!(player instanceof ServerPlayerEntity)) {
                    continue;
                }
                totalDiscovered += manager.scanStructuresFromFrames(level, player.blockPosition(), DISCOVERY_RADIUS);
            }
            TekVillagePerfStats.recordStructureScan(totalDiscovered);
            if (totalDiscovered > 0) {
                TekTopiaPort.LOGGER.debug("Structure discovery tick found {} frame markers in {}", totalDiscovered, level.dimension().location());
            }

            TekVillageStructure townHall = manager.getStructure(TekStructureType.TOWNHALL).orElse(null);
            if (townHall != null) {
                int dynamicRadius = Math.max(32, (int) Math.ceil(Math.sqrt(Math.max(1, townHall.getFloorTileCount())) * 4.0D));
                villageManager.upsertNearestVillage(townHall.getDoorInside(), dynamicRadius, level.getGameTime());
                runtime.saveRuntime(level);
            }
        }

        for (PlayerEntity player : level.players()) {
            if (!(player instanceof ServerPlayerEntity)) {
                continue;
            }
            if (Math.floorMod(level.getGameTime() + player.getUUID().hashCode(), VILLAGE_SYNC_TICK_INTERVAL) == 0L) {
                TekVillage nearest = villageManager.findNearestVillage(player.blockPosition()).orElse(null);
                TekNetwork.sendToPlayer(PacketVillage.createSnapshot(level, nearest, manager), (ServerPlayerEntity) player);
                TekVillagePerfStats.recordPacketSnapshot();
            }
        }
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
}
