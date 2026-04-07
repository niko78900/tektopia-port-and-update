package net.tangotek.tektopia.caps;

import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class VillageData implements IVillageData, Capability.IStorage<IVillageData> {
    private UUID villageId = UUID.randomUUID();
    private long childSpawnTime;
    private int professionSales;
    private boolean merchantCheckedToday;
    private boolean nomadCheckedToday;
    private boolean startingGifts;
    private boolean empty = true;

    @Override
    public UUID getVillageId() {
        return villageId;
    }

    @Override
    public void setVillageId(UUID id) {
        if (id != null) {
            villageId = id;
        }
    }

    @Override
    public long getChildSpawnTime() {
        return childSpawnTime;
    }

    @Override
    public void setChildSpawnTime(long worldTime) {
        childSpawnTime = worldTime;
        empty = false;
    }

    @Override
    public int getProfessionSales() {
        return professionSales;
    }

    @Override
    public void incrementProfessionSales() {
        professionSales++;
        empty = false;
    }

    @Override
    public boolean isMerchantCheckedToday() {
        return merchantCheckedToday;
    }

    @Override
    public void setMerchantCheckedToday(boolean checked) {
        merchantCheckedToday = checked;
        empty = false;
    }

    @Override
    public boolean isNomadCheckedToday() {
        return nomadCheckedToday;
    }

    @Override
    public void setNomadCheckedToday(boolean checked) {
        nomadCheckedToday = checked;
        empty = false;
    }

    @Override
    public boolean hasStartingGifts() {
        return startingGifts;
    }

    @Override
    public void setStartingGifts(boolean value) {
        startingGifts = value;
        empty = false;
    }

    @Override
    public boolean isEmpty() {
        return empty;
    }

    @Override
    public void writeNBT(CompoundNBT nbt) {
        nbt.putUUID("villageId", villageId);
        nbt.putLong("childSpawnTime", childSpawnTime);
        nbt.putInt("professionSales", professionSales);
        nbt.putBoolean("merchantCheckedToday", merchantCheckedToday);
        nbt.putBoolean("nomadCheckedToday", nomadCheckedToday);
        nbt.putBoolean("startingGifts", startingGifts);
        nbt.putBoolean("empty", empty);
    }

    @Override
    public void readNBT(CompoundNBT nbt) {
        if (nbt.hasUUID("villageId")) {
            villageId = nbt.getUUID("villageId");
        }
        childSpawnTime = nbt.getLong("childSpawnTime");
        professionSales = nbt.getInt("professionSales");
        merchantCheckedToday = nbt.getBoolean("merchantCheckedToday");
        nomadCheckedToday = nbt.getBoolean("nomadCheckedToday");
        startingGifts = nbt.getBoolean("startingGifts");
        empty = nbt.contains("empty") && nbt.getBoolean("empty");
    }

    @Override
    public INBT writeNBT(Capability<IVillageData> capability, IVillageData instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        instance.writeNBT(nbt);
        return nbt;
    }

    @Override
    public void readNBT(Capability<IVillageData> capability, IVillageData instance, Direction side, INBT nbt) {
        if (nbt instanceof CompoundNBT) {
            instance.readNBT((CompoundNBT) nbt);
        }
    }
}
