package net.tangotek.tektopia.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.village.TekVillageRuntime;
import net.tangotek.tektopia.village.TekVillageStructureManager;

public class TekStructureEvents {
    private static final int DISCOVERY_RADIUS = 64;
    private static final long DISCOVERY_TICK_INTERVAL = 200L;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isClientSide() || !(event.world instanceof ServerWorld)) {
            return;
        }
        ServerWorld level = (ServerWorld) event.world;
        if (level.getGameTime() % DISCOVERY_TICK_INTERVAL != 0L) {
            return;
        }

        TekVillageStructureManager manager = TekVillageRuntime.get().managerFor(level);
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
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!(event.getWorld() instanceof ServerWorld)) {
            return;
        }
        ServerWorld level = (ServerWorld) event.getWorld();
        TekVillageRuntime.get().clear(level.dimension());
    }
}
