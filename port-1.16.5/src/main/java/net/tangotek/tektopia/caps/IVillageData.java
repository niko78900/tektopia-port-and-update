package net.tangotek.tektopia.caps;

import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;

public interface IVillageData {
    UUID getVillageId();

    void setVillageId(UUID id);

    long getChildSpawnTime();

    void setChildSpawnTime(long worldTime);

    int getProfessionSales();

    void incrementProfessionSales();

    boolean isMerchantCheckedToday();

    void setMerchantCheckedToday(boolean checked);

    boolean isNomadCheckedToday();

    void setNomadCheckedToday(boolean checked);

    boolean hasStartingGifts();

    void setStartingGifts(boolean value);

    boolean isEmpty();

    void writeNBT(CompoundNBT nbt);

    void readNBT(CompoundNBT nbt);
}
