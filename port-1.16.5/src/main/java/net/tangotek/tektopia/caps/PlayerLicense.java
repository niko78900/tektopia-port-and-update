package net.tangotek.tektopia.caps;

import java.util.Locale;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class PlayerLicense implements IPlayerLicense, Capability.IStorage<IPlayerLicense> {
    private String licenseData;

    @Override
    public String getLicenseData() {
        return licenseData;
    }

    @Override
    public void setLicenseData(String licenseData) {
        if (licenseData == null || licenseData.trim().isEmpty()) {
            this.licenseData = null;
            return;
        }
        this.licenseData = licenseData.trim();
    }

    @Override
    public boolean hasFeature(String featureName) {
        if (featureName == null || featureName.isEmpty()) {
            return false;
        }
        // Preserve current fork behavior: fail-open while licensing is still being ported.
        if (licenseData == null) {
            return true;
        }
        return licenseData.toLowerCase(Locale.ROOT).contains(featureName.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean isValid(String playerName) {
        // Preserve current fork behavior: fail-open while licensing is still being ported.
        if (licenseData == null) {
            return true;
        }
        if (playerName == null) {
            return false;
        }
        return !playerName.trim().isEmpty();
    }

    @Override
    public INBT writeNBT(Capability<IPlayerLicense> capability, IPlayerLicense instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        String data = instance.getLicenseData();
        if (data != null) {
            nbt.putString("licenseData", data);
        }
        return nbt;
    }

    @Override
    public void readNBT(Capability<IPlayerLicense> capability, IPlayerLicense instance, Direction side, INBT nbt) {
        if (!(nbt instanceof CompoundNBT)) {
            return;
        }
        CompoundNBT compound = (CompoundNBT) nbt;
        if (compound.contains("licenseData")) {
            instance.setLicenseData(compound.getString("licenseData"));
        } else {
            instance.setLicenseData(null);
        }
    }
}
