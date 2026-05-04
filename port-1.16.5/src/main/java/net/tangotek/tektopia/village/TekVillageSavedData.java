package net.tangotek.tektopia.village;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.tangotek.tektopia.TekTopiaPort;

public class TekVillageSavedData extends WorldSavedData {
    public static final String DATA_NAME = TekTopiaPort.MODID + "_village_runtime";
    private static final int DATA_VERSION = 3;

    private int dataVersion = DATA_VERSION;
    private CompoundNBT villagesTag = new CompoundNBT();
    private CompoundNBT structuresTag = new CompoundNBT();
    private CompoundNBT economyTag = new CompoundNBT();

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
        this.dataVersion = nbt.contains("dataVersion", 3) ? nbt.getInt("dataVersion") : 1;
        this.villagesTag = nbt.contains("villages", 10) ? nbt.getCompound("villages").copy() : new CompoundNBT();
        this.structuresTag = nbt.contains("structures", 10) ? nbt.getCompound("structures").copy() : new CompoundNBT();
        this.economyTag = nbt.contains("economy", 10) ? nbt.getCompound("economy").copy() : new CompoundNBT();
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putInt("dataVersion", DATA_VERSION);
        nbt.put("villages", this.villagesTag.copy());
        nbt.put("structures", this.structuresTag.copy());
        nbt.put("economy", this.economyTag.copy());
        return nbt;
    }

    public void copyToManagers(ServerWorld level, TekVillageManager villageManager, TekVillageStructureManager structureManager) {
        villageManager.load(this.villagesTag.copy());
        structureManager.load(level, this.structuresTag.copy());
        TekVillageEconomy.loadReservations(this.economyTag.copy());
    }

    public void copyFromManagers(TekVillageManager villageManager, TekVillageStructureManager structureManager) {
        CompoundNBT serializedVillages = villageManager.save(new CompoundNBT());
        CompoundNBT serializedStructures = structureManager.save(new CompoundNBT());
        CompoundNBT serializedEconomy = TekVillageEconomy.saveReservations();
        if (!serializedVillages.equals(this.villagesTag)
                || !serializedStructures.equals(this.structuresTag)
                || !serializedEconomy.equals(this.economyTag)
                || this.dataVersion != DATA_VERSION) {
            this.villagesTag = serializedVillages;
            this.structuresTag = serializedStructures;
            this.economyTag = serializedEconomy;
            this.dataVersion = DATA_VERSION;
            this.setDirty();
        }
    }
}
