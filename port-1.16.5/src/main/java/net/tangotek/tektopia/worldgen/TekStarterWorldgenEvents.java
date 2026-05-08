package net.tangotek.tektopia.worldgen;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.structures.TekVillageStructure;
import net.tangotek.tektopia.village.TekVillageManager;
import net.tangotek.tektopia.village.TekVillageRuntime;
import net.tangotek.tektopia.village.TekVillageStructureManager;

public class TekStarterWorldgenEvents {
    private static final boolean STARTER_GENERATION_ENABLED = true;
    private static final long STARTER_GENERATION_DELAY_TICKS = 200L;
    private static final long STARTER_GENERATION_CHECK_INTERVAL = 200L;
    private static final int STARTER_OFFSET = 64;
    private static int biomeHookCount;

    @SubscribeEvent
    public void onBiomeLoading(BiomeLoadingEvent event) {
        if (STARTER_GENERATION_ENABLED) {
            biomeHookCount++;
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!STARTER_GENERATION_ENABLED
                || event.phase != TickEvent.Phase.END
                || event.world.isClientSide()
                || !(event.world instanceof ServerWorld)) {
            return;
        }
        ServerWorld level = (ServerWorld) event.world;
        if (level.dimension() != World.OVERWORLD
                || level.getGameTime() < STARTER_GENERATION_DELAY_TICKS
                || level.getGameTime() % STARTER_GENERATION_CHECK_INTERVAL != 0L) {
            return;
        }

        TekWorldgenSavedData data = TekWorldgenSavedData.get(level);
        if (data.isStarterGenerated()) {
            return;
        }

        BlockPos spawn = level.getSharedSpawnPos();
        BlockPos origin = level.getHeightmapPos(
                Heightmap.Type.WORLD_SURFACE,
                spawn.offset(STARTER_OFFSET, 0, STARTER_OFFSET)
        ).immutable();
        int generated = this.generateStarterCluster(level, origin);
        data.markStarterGenerated(origin, generated, level.getGameTime());
        TekTopiaPort.LOGGER.info(
                "Generated TekTopia starter worldgen cluster at {} structures={}",
                origin.toShortString(),
                generated
        );
    }

    public static String formatStatus(ServerWorld level) {
        TekWorldgenSavedData data = TekWorldgenSavedData.get(level);
        return "enabled=" + STARTER_GENERATION_ENABLED
                + " biomeHooks=" + biomeHookCount
                + " " + data.formatStatus();
    }

    private int generateStarterCluster(ServerWorld level, BlockPos origin) {
        TekVillageRuntime runtime = TekVillageRuntime.get();
        TekVillageStructureManager structureManager = runtime.managerFor(level);
        TekVillageManager villageManager = runtime.villageManagerFor(level);
        int generated = 0;

        generated += generateOne(level, structureManager, villageManager, TekStructureType.TOWNHALL, origin, Direction.SOUTH, true);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.STORAGE, origin.east(12), Direction.SOUTH, false);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.HOME, origin.west(12), Direction.SOUTH, false);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.FARM, origin.south(12), Direction.NORTH, false);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.MINESHAFT, origin.north(12), Direction.SOUTH, false);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.SCHOOL, origin.east(24), Direction.SOUTH, false);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.TAVERN, origin.west(24), Direction.SOUTH, false);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.LIBRARY, origin.south(24), Direction.NORTH, false);

        runtime.saveRuntime(level);
        return generated;
    }

    private static int generateOne(
            ServerWorld level,
            TekVillageStructureManager structureManager,
            TekVillageManager villageManager,
            TekStructureType type,
            BlockPos pos,
            Direction facing,
            boolean createVillage
    ) {
        TekStarterStructureGenerator.Result result = TekStarterStructureGenerator.generate(level, type, pos, facing);
        TekVillageStructure structure = structureManager.scanStructure(
                level,
                type,
                result.getDoorInside(),
                result.getSignFacing()
        );
        if (createVillage && structure.isValid()) {
            int radius = Math.max(48, (int) Math.ceil(Math.sqrt(Math.max(1, structure.getFloorTileCount())) * 5.0D));
            villageManager.upsertNearestVillage(structure.getDoorInside(), radius, level.getGameTime());
        }
        return structure.isValid() ? 1 : 0;
    }
}
