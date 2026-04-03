/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.CapabilityInject
 *  net.minecraftforge.common.capabilities.ICapabilitySerializable
 */
package net.tangotek.tektopia.caps;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.tangotek.tektopia.caps.IPlayerLicense;

public class PlayerLicenseProvider
implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(value=IPlayerLicense.class)
    public static final Capability<IPlayerLicense> PLAYER_LICENSE_CAPABILITY = null;
    private IPlayerLicense instance = (IPlayerLicense)PLAYER_LICENSE_CAPABILITY.getDefaultInstance();

    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == PLAYER_LICENSE_CAPABILITY;
    }

    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == PLAYER_LICENSE_CAPABILITY) {
            return (T)PLAYER_LICENSE_CAPABILITY.cast((Object)this.instance);
        }
        return null;
    }

    public NBTBase serializeNBT() {
        return PLAYER_LICENSE_CAPABILITY.getStorage().writeNBT(PLAYER_LICENSE_CAPABILITY, (Object)this.instance, null);
    }

    public void deserializeNBT(NBTBase nbt) {
        PLAYER_LICENSE_CAPABILITY.getStorage().readNBT(PLAYER_LICENSE_CAPABILITY, (Object)this.instance, null, nbt);
    }
}

