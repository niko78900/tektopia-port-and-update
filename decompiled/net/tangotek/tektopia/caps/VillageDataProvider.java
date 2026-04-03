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
import net.tangotek.tektopia.caps.IVillageData;

public class VillageDataProvider
implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(value=IVillageData.class)
    public static final Capability<IVillageData> VILLAGE_DATA_CAPABILITY = null;
    private IVillageData instance = (IVillageData)VILLAGE_DATA_CAPABILITY.getDefaultInstance();

    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == VILLAGE_DATA_CAPABILITY;
    }

    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return (T)(capability == VILLAGE_DATA_CAPABILITY ? VILLAGE_DATA_CAPABILITY.cast((Object)this.instance) : null);
    }

    public NBTBase serializeNBT() {
        return VILLAGE_DATA_CAPABILITY.getStorage().writeNBT(VILLAGE_DATA_CAPABILITY, (Object)this.instance, null);
    }

    public void deserializeNBT(NBTBase nbt) {
        VILLAGE_DATA_CAPABILITY.getStorage().readNBT(VILLAGE_DATA_CAPABILITY, (Object)this.instance, null, nbt);
    }
}

