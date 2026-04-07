package net.tangotek.tektopia.caps;

public interface IPlayerLicense {
    String getLicenseData();

    void setLicenseData(String licenseData);

    boolean hasFeature(String featureName);

    boolean isValid(String playerName);
}
