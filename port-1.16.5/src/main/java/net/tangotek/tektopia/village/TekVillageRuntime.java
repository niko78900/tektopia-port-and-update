package net.tangotek.tektopia.village;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public final class TekVillageRuntime {
    private static final TekVillageRuntime INSTANCE = new TekVillageRuntime();

    private final Object loadLock = new Object();
    private final Map<RegistryKey<World>, TekVillageStructureManager> structureManagers =
            new ConcurrentHashMap<>();
    private final Map<RegistryKey<World>, TekVillageManager> villageManagers =
            new ConcurrentHashMap<>();

    private TekVillageRuntime() {
    }

    public static TekVillageRuntime get() {
        return INSTANCE;
    }

    public TekVillageStructureManager managerFor(ServerWorld level) {
        this.ensureLoaded(level);
        return this.structureManagers.computeIfAbsent(level.dimension(), ignored -> new TekVillageStructureManager());
    }

    public Optional<TekVillageStructureManager> getManager(RegistryKey<World> dimension) {
        return Optional.ofNullable(this.structureManagers.get(dimension));
    }

    public TekVillageManager villageManagerFor(ServerWorld level) {
        this.ensureLoaded(level);
        return this.villageManagers.computeIfAbsent(level.dimension(), ignored -> new TekVillageManager());
    }

    public void saveVillageManager(ServerWorld level) {
        this.saveRuntime(level);
    }

    public void saveRuntime(ServerWorld level) {
        this.ensureLoaded(level);
        TekVillageSavedData.get(level).copyFromManagers(
                this.villageManagerFor(level),
                this.managerFor(level)
        );
    }

    public Optional<TekVillageManager> getVillageManager(RegistryKey<World> dimension) {
        return Optional.ofNullable(this.villageManagers.get(dimension));
    }

    public void clear(RegistryKey<World> dimension) {
        this.structureManagers.remove(dimension);
        this.villageManagers.remove(dimension);
    }

    public void clearStructureCache(RegistryKey<World> dimension) {
        this.structureManagers.remove(dimension);
    }

    public void clearVillageCache(RegistryKey<World> dimension) {
        this.villageManagers.remove(dimension);
    }

    public void clearAll() {
        this.structureManagers.clear();
        this.villageManagers.clear();
    }

    private void ensureLoaded(ServerWorld level) {
        RegistryKey<World> dimension = level.dimension();
        if (this.villageManagers.containsKey(dimension) && this.structureManagers.containsKey(dimension)) {
            return;
        }
        synchronized (this.loadLock) {
            if (this.villageManagers.containsKey(dimension) && this.structureManagers.containsKey(dimension)) {
                return;
            }
            TekVillageManager villageManager = this.villageManagers.computeIfAbsent(dimension, ignored -> new TekVillageManager());
            TekVillageStructureManager structureManager = this.structureManagers.computeIfAbsent(dimension, ignored -> new TekVillageStructureManager());
            TekVillageSavedData.get(level).copyToManagers(level, villageManager, structureManager);
        }
    }
}
