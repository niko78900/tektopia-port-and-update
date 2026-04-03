/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.Capability$IStorage
 */
package net.tangotek.tektopia.caps;

import com.websina.license.LicenseManager;
import com.websina.license.LicenseManagerTek;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.tangotek.tektopia.LicenseTracker;
import net.tangotek.tektopia.caps.IPlayerLicense;

public class PlayerLicense
implements IPlayerLicense,
Capability.IStorage<IPlayerLicense> {
    private LicenseManager licenseManager;

    @Override
    public String getLicenseData() {
        if (this.licenseManager != null) {
            return this.licenseManager.getLicense();
        }
        return null;
    }

    @Override
    public void setLicenseData(String licData) {
        this.licenseManager = new LicenseManagerTek(licData);
        try {
            if (!this.licenseManager.isValid()) {
                this.licenseManager = null;
            }
        }
        catch (GeneralSecurityException e) {
            System.err.println("GeneralSecurityException validating license key.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasFeature(LicenseTracker.Feature feature) {
        if (feature == null) {
            return false;
        }
        // QoL: fail-open when no license is present so optional content is still usable.
        if (this.licenseManager == null) {
            return true;
        }
        return this.licenseManager.getFeature(feature.getName()) != null;
    }

    @Override
    public boolean isValid(String name) {
        if (this.licenseManager == null) {
            return true;
        }
        if (name == null) {
            return false;
        }
        if (name.toLowerCase().startsWith("player")) {
            return true;
        }
        String ign = this.licenseManager.getFeature("IGN");
        return ign != null && ign.toLowerCase().equals(name.toLowerCase());
    }

    @Nullable
    public NBTBase writeNBT(Capability<IPlayerLicense> capability, IPlayerLicense instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        return compound;
    }

    public void readNBT(Capability<IPlayerLicense> capability, IPlayerLicense instance, EnumFacing side, NBTBase nbt) {
    }
}
