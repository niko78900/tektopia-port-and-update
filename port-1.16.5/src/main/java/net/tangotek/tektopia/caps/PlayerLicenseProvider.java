package net.tangotek.tektopia.caps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerLicenseProvider implements ICapabilitySerializable<INBT> {
    @CapabilityInject(IPlayerLicense.class)
    public static final Capability<IPlayerLicense> PLAYER_LICENSE_CAPABILITY = null;

    private final IPlayerLicense instance = new PlayerLicense();
    private final LazyOptional<IPlayerLicense> holder = LazyOptional.of(() -> instance);

    public void invalidate() {
        holder.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_LICENSE_CAPABILITY) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        if (PLAYER_LICENSE_CAPABILITY == null) {
            return new net.minecraft.nbt.CompoundNBT();
        }
        return PLAYER_LICENSE_CAPABILITY.getStorage().writeNBT(PLAYER_LICENSE_CAPABILITY, instance, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        if (PLAYER_LICENSE_CAPABILITY == null) {
            return;
        }
        PLAYER_LICENSE_CAPABILITY.getStorage().readNBT(PLAYER_LICENSE_CAPABILITY, instance, null, nbt);
    }
}
