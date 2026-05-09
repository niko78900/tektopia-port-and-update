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
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageManager;
import net.tangotek.tektopia.village.TekVillageRuntime;
import net.tangotek.tektopia.village.TekVillageStructureManager;

public class TekStarterWorldgenEvents {
    private static final boolean STARTER_GENERATION_ENABLED = true;
    private static final boolean STARTER_GENERATION_DEBUG_LOGGING = false;
    private static final long STARTER_GENERATION_DELAY_TICKS = 200L;
    private static final long STARTER_GENERATION_CHECK_INTERVAL = 200L;
    private static final long STARTER_FAILURE_RETRY_TICKS = 24_000L;
    private static final int STARTER_OFFSET = 64;
    private static final int STARTER_MIN_SPAWN_DISTANCE = 48;
    private static final int STARTER_STRUCTURE_SPACING = 32;
    private static final int STARTER_OUTER_STRUCTURE_SPACING = 64;
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
            data.recordDuplicatePrevention(level.getGameTime());
            return;
        }
        if (data.isInFailureBackoff(level.getGameTime(), STARTER_FAILURE_RETRY_TICKS)) {
            return;
        }

        BlockPos spawn = level.getSharedSpawnPos();
        BlockPos target = spawn.offset(
                Math.max(STARTER_OFFSET, STARTER_MIN_SPAWN_DISTANCE),
                0,
                Math.max(STARTER_OFFSET, STARTER_MIN_SPAWN_DISTANCE)
        );
        BlockPos origin = level.getHeightmapPos(
                Heightmap.Type.WORLD_SURFACE,
                target
        ).immutable();
        data.recordGenerationAttempt(origin, level.getGameTime(), level.dimension().location().toString());
        int generated = this.generateStarterCluster(level, origin);
        if (generated <= 0) {
            data.recordGenerationFailure(origin, level.getGameTime(), "no_valid_structures");
            TekTopiaPort.LOGGER.warn(
                    "TekTopia starter worldgen attempt at {} produced no valid structures; retrying after {} ticks",
                    origin.toShortString(),
                    STARTER_FAILURE_RETRY_TICKS
            );
            return;
        }
        data.markStarterGenerated(origin, generated, level.getGameTime());
        if (STARTER_GENERATION_DEBUG_LOGGING) {
            TekTopiaPort.LOGGER.info(
                    "Generated TekTopia starter worldgen cluster at {} structures={}",
                    origin.toShortString(),
                    generated
            );
        }
    }

    public static String formatStatus(ServerWorld level) {
        TekWorldgenSavedData data = TekWorldgenSavedData.get(level);
        return "enabled=" + STARTER_GENERATION_ENABLED
                + " dimension=" + World.OVERWORLD.location()
                + " delayTicks=" + STARTER_GENERATION_DELAY_TICKS
                + " checkIntervalTicks=" + STARTER_GENERATION_CHECK_INTERVAL
                + " failureRetryTicks=" + STARTER_FAILURE_RETRY_TICKS
                + " offset=" + STARTER_OFFSET
                + " minSpawnDistance=" + STARTER_MIN_SPAWN_DISTANCE
                + " structureSpacing=" + STARTER_STRUCTURE_SPACING
                + " outerStructureSpacing=" + STARTER_OUTER_STRUCTURE_SPACING
                + " debugLogging=" + STARTER_GENERATION_DEBUG_LOGGING
                + " biomeHooks=" + biomeHookCount
                + " " + data.formatStatus();
    }

    private int generateStarterCluster(ServerWorld level, BlockPos origin) {
        TekVillageRuntime runtime = TekVillageRuntime.get();
        TekVillageStructureManager structureManager = runtime.managerFor(level);
        TekVillageManager villageManager = runtime.villageManagerFor(level);
        int generated = 0;

        generated += generateOne(level, structureManager, villageManager, TekStructureType.TOWNHALL, origin, Direction.SOUTH, true, null);
        TekVillage starterVillage = villageManager.findNearestVillage(origin)
                .filter(village -> village.contains(origin))
                .orElse(null);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.STORAGE, origin.east(STARTER_STRUCTURE_SPACING), Direction.WEST, false, starterVillage);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.HOME, origin.west(STARTER_STRUCTURE_SPACING), Direction.EAST, false, starterVillage);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.FARM, origin.south(STARTER_STRUCTURE_SPACING), Direction.NORTH, false, starterVillage);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.MINESHAFT, origin.north(STARTER_STRUCTURE_SPACING), Direction.SOUTH, false, starterVillage);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.SCHOOL, origin.east(STARTER_OUTER_STRUCTURE_SPACING), Direction.WEST, false, starterVillage);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.TAVERN, origin.west(STARTER_OUTER_STRUCTURE_SPACING), Direction.EAST, false, starterVillage);
        generated += generateOne(level, structureManager, villageManager, TekStructureType.LIBRARY, origin.south(STARTER_OUTER_STRUCTURE_SPACING), Direction.NORTH, false, starterVillage);

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
            boolean createVillage,
            TekVillage tokenVillage
    ) {
        TekStarterStructureGenerator.Result result = TekStarterStructureGenerator.generate(level, type, pos, facing, tokenVillage);
        if (result.getPlacedBlocks() <= 0) {
            return 0;
        }
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
