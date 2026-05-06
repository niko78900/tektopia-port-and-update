package net.tangotek.tektopia.village;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.tangotek.tektopia.TekTopiaPort;

public class TekVillageSavedData extends WorldSavedData {
    public static final String DATA_NAME = TekTopiaPort.MODID + "_village_runtime";
    public static final int DATA_VERSION = 3;

    private int dataVersion = DATA_VERSION;
    private CompoundNBT villagesTag = new CompoundNBT();
    private CompoundNBT structuresTag = new CompoundNBT();

    public TekVillageSavedData() {
        super(DATA_NAME);
    }

    public TekVillageSavedData(CompoundNBT nbt) {
        this();
        this.load(nbt);
    }

    public static TekVillageSavedData get(ServerWorld level) {
        return level.getDataStorage().computeIfAbsent(TekVillageSavedData::new, DATA_NAME);
    }

    @Override
    public void load(CompoundNBT nbt) {
        int loadedVersion = nbt.contains("dataVersion", 3) ? nbt.getInt("dataVersion") : 1;
        CompoundNBT migrated = this.migrate(nbt, loadedVersion);
        this.dataVersion = DATA_VERSION;
        this.villagesTag = migrated.getCompound("villages").copy();
        this.structuresTag = migrated.getCompound("structures").copy();
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putInt("dataVersion", DATA_VERSION);
        nbt.put("villages", this.villagesTag.copy());
        nbt.put("structures", this.structuresTag.copy());
        return nbt;
    }

    public void copyToManagers(ServerWorld level, TekVillageManager villageManager, TekVillageStructureManager structureManager) {
        villageManager.load(this.villagesTag.copy());
        structureManager.load(level, this.structuresTag.copy());
    }

    public void copyFromManagers(TekVillageManager villageManager, TekVillageStructureManager structureManager) {
        CompoundNBT serializedVillages = villageManager.save(new CompoundNBT());
        CompoundNBT serializedStructures = structureManager.save(new CompoundNBT());
        if (!serializedVillages.equals(this.villagesTag) || !serializedStructures.equals(this.structuresTag) || this.dataVersion != DATA_VERSION) {
            this.villagesTag = serializedVillages;
            this.structuresTag = serializedStructures;
            this.dataVersion = DATA_VERSION;
            this.setDirty();
        }
    }

    private CompoundNBT migrate(CompoundNBT input, int loadedVersion) {
        CompoundNBT migrated = input == null ? new CompoundNBT() : input.copy();
        if (!migrated.contains("villages", 10)) {
            migrated.put("villages", new CompoundNBT());
        }
        if (loadedVersion < 2 || !migrated.contains("structures", 10)) {
            migrated.put("structures", new CompoundNBT());
        }
        if (loadedVersion < 3) {
            migrated.putInt("dataVersion", DATA_VERSION);
        }
        return migrated;
    }
}
