package net.tangotek.tektopia.worldgen;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.tangotek.tektopia.TekTopiaPort;

public class TekWorldgenSavedData extends WorldSavedData {
    public static final String DATA_NAME = TekTopiaPort.MODID + "_worldgen_runtime";
    private static final int DATA_VERSION = 1;

    private int dataVersion = DATA_VERSION;
    private boolean starterGenerated;
    private BlockPos starterPos = BlockPos.ZERO;
    private int generatedStructures;
    private long generatedTime;

    public TekWorldgenSavedData() {
        super(DATA_NAME);
    }

    public TekWorldgenSavedData(CompoundNBT nbt) {
        this();
        this.load(nbt);
    }

    public static TekWorldgenSavedData get(ServerWorld level) {
        return level.getDataStorage().computeIfAbsent(TekWorldgenSavedData::new, DATA_NAME);
    }

    @Override
    public void load(CompoundNBT nbt) {
        this.dataVersion = nbt.contains("dataVersion", 3) ? nbt.getInt("dataVersion") : 0;
        this.starterGenerated = nbt.getBoolean("starterGenerated");
        this.starterPos = nbt.contains("starterPos", 4) ? BlockPos.of(nbt.getLong("starterPos")) : BlockPos.ZERO;
        this.generatedStructures = Math.max(0, nbt.getInt("generatedStructures"));
        this.generatedTime = Math.max(0L, nbt.getLong("generatedTime"));
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putInt("dataVersion", DATA_VERSION);
        nbt.putBoolean("starterGenerated", this.starterGenerated);
        nbt.putLong("starterPos", this.starterPos.asLong());
        nbt.putInt("generatedStructures", this.generatedStructures);
        nbt.putLong("generatedTime", this.generatedTime);
        return nbt;
    }

    public boolean isStarterGenerated() {
        return this.starterGenerated;
    }

    public void markStarterGenerated(BlockPos pos, int structureCount, long gameTime) {
        this.starterGenerated = true;
        this.starterPos = pos == null ? BlockPos.ZERO : pos.immutable();
        this.generatedStructures = Math.max(0, structureCount);
        this.generatedTime = Math.max(0L, gameTime);
        this.dataVersion = DATA_VERSION;
        this.setDirty();
    }

    public String formatStatus() {
        return "starterGenerated=" + this.starterGenerated
                + " starterPos=" + (this.starterPos == null ? "-" : this.starterPos.toShortString())
                + " generatedStructures=" + this.generatedStructures
                + " generatedTime=" + this.generatedTime
                + " dataVersion=" + this.dataVersion;
    }
}
