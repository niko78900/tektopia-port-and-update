package net.tangotek.tektopia.caps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class VillageDataProvider implements ICapabilitySerializable<INBT> {
    @CapabilityInject(IVillageData.class)
    public static final Capability<IVillageData> VILLAGE_DATA_CAPABILITY = null;

    private final IVillageData instance = new VillageData();
    private final LazyOptional<IVillageData> holder = LazyOptional.of(() -> instance);

    public void invalidate() {
        holder.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == VILLAGE_DATA_CAPABILITY) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        if (VILLAGE_DATA_CAPABILITY == null) {
            return new CompoundNBT();
        }
        return VILLAGE_DATA_CAPABILITY.getStorage().writeNBT(VILLAGE_DATA_CAPABILITY, instance, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        if (VILLAGE_DATA_CAPABILITY == null) {
            return;
        }
        VILLAGE_DATA_CAPABILITY.getStorage().readNBT(VILLAGE_DATA_CAPABILITY, instance, null, nbt);
    }
}
