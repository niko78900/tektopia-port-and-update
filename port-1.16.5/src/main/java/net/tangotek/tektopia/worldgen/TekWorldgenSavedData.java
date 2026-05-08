package net.tangotek.tektopia.worldgen;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.tangotek.tektopia.TekTopiaPort;

public class TekWorldgenSavedData extends WorldSavedData {
    public static final String DATA_NAME = TekTopiaPort.MODID + "_worldgen_runtime";
    private static final int DATA_VERSION = 2;

    private int dataVersion = DATA_VERSION;
    private boolean starterGenerated;
    private BlockPos starterPos = BlockPos.ZERO;
    private int generatedStructures;
    private long generatedTime;
    private BlockPos lastAttemptPos = BlockPos.ZERO;
    private long lastAttemptTime;
    private String lastAttemptDimension = "";
    private int generationFailures;
    private long lastFailureTime;
    private String lastFailureReason = "";
    private int duplicatePreventionHits;

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
        this.lastAttemptPos = nbt.contains("lastAttemptPos", 4) ? BlockPos.of(nbt.getLong("lastAttemptPos")) : BlockPos.ZERO;
        this.lastAttemptTime = Math.max(0L, nbt.getLong("lastAttemptTime"));
        this.lastAttemptDimension = nbt.getString("lastAttemptDimension");
        this.generationFailures = Math.max(0, nbt.getInt("generationFailures"));
        this.lastFailureTime = Math.max(0L, nbt.getLong("lastFailureTime"));
        this.lastFailureReason = nbt.getString("lastFailureReason");
        this.duplicatePreventionHits = Math.max(0, nbt.getInt("duplicatePreventionHits"));
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putInt("dataVersion", DATA_VERSION);
        nbt.putBoolean("starterGenerated", this.starterGenerated);
        nbt.putLong("starterPos", this.starterPos.asLong());
        nbt.putInt("generatedStructures", this.generatedStructures);
        nbt.putLong("generatedTime", this.generatedTime);
        nbt.putLong("lastAttemptPos", this.lastAttemptPos.asLong());
        nbt.putLong("lastAttemptTime", this.lastAttemptTime);
        nbt.putString("lastAttemptDimension", this.lastAttemptDimension == null ? "" : this.lastAttemptDimension);
        nbt.putInt("generationFailures", this.generationFailures);
        nbt.putLong("lastFailureTime", this.lastFailureTime);
        nbt.putString("lastFailureReason", this.lastFailureReason == null ? "" : this.lastFailureReason);
        nbt.putInt("duplicatePreventionHits", this.duplicatePreventionHits);
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
        this.lastFailureReason = "";
        this.lastFailureTime = 0L;
        this.dataVersion = DATA_VERSION;
        this.setDirty();
    }

    public void recordGenerationAttempt(BlockPos pos, long gameTime, String dimensionKey) {
        this.lastAttemptPos = pos == null ? BlockPos.ZERO : pos.immutable();
        this.lastAttemptTime = Math.max(0L, gameTime);
        this.lastAttemptDimension = dimensionKey == null ? "" : dimensionKey;
        this.dataVersion = DATA_VERSION;
        this.setDirty();
    }

    public void recordGenerationFailure(BlockPos pos, long gameTime, String reason) {
        this.lastAttemptPos = pos == null ? BlockPos.ZERO : pos.immutable();
        this.lastAttemptTime = Math.max(0L, gameTime);
        this.generationFailures++;
        this.lastFailureTime = Math.max(0L, gameTime);
        this.lastFailureReason = reason == null ? "" : reason;
        this.dataVersion = DATA_VERSION;
        this.setDirty();
    }

    public boolean isInFailureBackoff(long gameTime, long retryTicks) {
        return this.lastFailureTime > 0L
                && retryTicks > 0L
                && gameTime >= this.lastFailureTime
                && gameTime - this.lastFailureTime < retryTicks;
    }

    public void recordDuplicatePrevention(long gameTime) {
        this.duplicatePreventionHits++;
        this.lastAttemptTime = Math.max(0L, gameTime);
        this.dataVersion = DATA_VERSION;
        if (this.duplicatePreventionHits <= 5 || this.duplicatePreventionHits % 300 == 0) {
            this.setDirty();
        }
    }

    public String formatStatus() {
        return "starterGenerated=" + this.starterGenerated
                + " starterPos=" + (this.starterPos == null ? "-" : this.starterPos.toShortString())
                + " generatedStructures=" + this.generatedStructures
                + " generatedTime=" + this.generatedTime
                + " lastAttemptPos=" + (this.lastAttemptPos == null ? "-" : this.lastAttemptPos.toShortString())
                + " lastAttemptTime=" + this.lastAttemptTime
                + " lastAttemptDimension=" + this.lastAttemptDimension
                + " generationFailures=" + this.generationFailures
                + " lastFailureTime=" + this.lastFailureTime
                + " lastFailureReason=" + (this.lastFailureReason == null || this.lastFailureReason.isEmpty() ? "-" : this.lastFailureReason)
                + " duplicatePreventionHits=" + this.duplicatePreventionHits
                + " dataVersion=" + this.dataVersion;
    }
}
