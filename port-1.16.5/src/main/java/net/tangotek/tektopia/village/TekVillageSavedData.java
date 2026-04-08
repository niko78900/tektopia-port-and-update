package net.tangotek.tektopia.village;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.tangotek.tektopia.TekTopiaPort;

public class TekVillageSavedData extends WorldSavedData {
    public static final String DATA_NAME = TekTopiaPort.MODID + "_village_runtime";

    private CompoundNBT villagesTag = new CompoundNBT();

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
        this.villagesTag = nbt.contains("villages", 10) ? nbt.getCompound("villages").copy() : new CompoundNBT();
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.put("villages", this.villagesTag.copy());
        return nbt;
    }

    public void copyToManager(TekVillageManager manager) {
        manager.load(this.villagesTag.copy());
    }

    public void copyFromManager(TekVillageManager manager) {
        CompoundNBT serialized = manager.save(new CompoundNBT());
        if (!serialized.equals(this.villagesTag)) {
            this.villagesTag = serialized;
            this.setDirty();
        }
    }
}
