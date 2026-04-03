/*
 * Decompiled with CFR 0.152.
 */
package net.tangotek.tektopia.caps;

import net.tangotek.tektopia.LicenseTracker;

public interface IPlayerLicense {
    public String getLicenseData();

    public void setLicenseData(String var1);

    public boolean hasFeature(LicenseTracker.Feature var1);

    public boolean isValid(String var1);
}

