/*
 * Decompiled with CFR 0.152.
 */
package com.websina.license;

import com.websina.license.License;
import com.websina.license.LicenseManager;
import com.websina.license.LicenseNotFoundException;
import com.websina.license.SignatureUtil;
import com.websina.util.ByteHex;
import com.websina.util.DateParser;
import java.security.GeneralSecurityException;
import java.util.Date;

public class LicenseManagerTek
extends LicenseManager {
    private static final String PUBLIC_KEY = "308201B73082012C06072A8648CE3804013082011F02818100FD7F53811D75122952DF4A9C2EECE4E7F611B7523CEF4400C31E3F80B6512669455D402251FB593D8D58FABFC5F5BA30F6CB9B556CD7813B801D346FF26660B76B9950A5A49F9FE8047B1022C24FBBA9D7FEB7C61BF83B57E7C6A8A6150F04FB83F6D3C51EC3023554135A169132F675F3AE2B61D72AEFF22203199DD14801C70215009760508F15230BCCB292B982A2EB840BF0581CF502818100F7E1A085D69B3DDECBBCAB5C36B857B97994AFBBFA3AEA82F9574C0B3D0782675159578EBAD4594FE67107108180B449167123E84C281613B7CF09328CC8A6E13C167A8B547C8D28E0A3AE1E2BB3A675916EA37F0BFA213562F1FB627A01243BCCA4F1BEA8519089A883DFE15AE59F06928B665E807B552564014C3BFECF492A0381840002818032BBD1DCB3628DF73FAEA5CDFC0851753D4D6D9CFE7D444F46E7F3346B9BBBA3202F8830A46A2F7FC35DB8F6AB6CE273DEBA8914221A10CE0D15AA142D00C4FC3FD78BDA441DB6C51D4A67AA28EB0E571DC91CAE64B21606A4142EEEC7F433509358E60F52205EBAE5E8029F75AF89D619995572D3E6CF65D5D9AA809208AFA9";
    private static final int MS_A_DAY = 86400000;
    private byte[] key;
    private License lic;

    public LicenseManagerTek(String licData) {
        try {
            this.lic = License.loadLicense(licData);
        }
        catch (LicenseNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            this.key = ByteHex.convert(PUBLIC_KEY);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LicenseManagerTek() {
        try {
            this.lic = License.loadLicense();
        }
        catch (LicenseNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            this.key = ByteHex.convert(PUBLIC_KEY);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isValid() throws GeneralSecurityException {
        return this.isLicenceValid(this.lic);
    }

    public boolean isLicenceValid(License lic) throws GeneralSecurityException {
        String signature = lic.getSignature();
        if (signature == null || signature.trim().length() == 0) {
            return false;
        }
        boolean valid = SignatureUtil.verify(lic.format(), ByteHex.convert(signature), this.key);
        if (!valid) {
            return false;
        }
        return this.daysLeft() >= 0;
    }

    @Override
    public int daysLeft() {
        String expiration = this.lic.getExpiration();
        if (expiration == null) {
            return -1;
        }
        if (expiration.trim().length() == 0 || expiration.indexOf("never") != -1) {
            return 0;
        }
        Date licDate = DateParser.toUtilDate(expiration);
        long time = licDate.getTime() - System.currentTimeMillis();
        int days = 1 + (int)(time / 86400000L);
        return days;
    }

    @Override
    public String getFeature(String name) {
        return this.lic.getFeature(name);
    }

    @Override
    public String getLicense() {
        return this.lic.getRawData();
    }
}

