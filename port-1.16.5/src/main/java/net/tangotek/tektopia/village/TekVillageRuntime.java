package net.tangotek.tektopia.village;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public final class TekVillageRuntime {
    private static final TekVillageRuntime INSTANCE = new TekVillageRuntime();

    private final Map<RegistryKey<World>, TekVillageStructureManager> managers =
            new ConcurrentHashMap<>();

    private TekVillageRuntime() {
    }

    public static TekVillageRuntime get() {
        return INSTANCE;
    }

    public TekVillageStructureManager managerFor(ServerWorld level) {
        return this.managers.computeIfAbsent(level.dimension(), ignored -> new TekVillageStructureManager());
    }

    public Optional<TekVillageStructureManager> getManager(RegistryKey<World> dimension) {
        return Optional.ofNullable(this.managers.get(dimension));
    }

    public void clear(RegistryKey<World> dimension) {
        this.managers.remove(dimension);
    }

    public void clearAll() {
        this.managers.clear();
    }
}
