/*
 * Decompiled with CFR 0.152.
 */
package com.websina.license;

import java.security.GeneralSecurityException;

public abstract class LicenseManager {
    public abstract boolean isValid() throws GeneralSecurityException;

    public abstract int daysLeft();

    public abstract String getFeature(String var1);

    public abstract String getLicense();
}

