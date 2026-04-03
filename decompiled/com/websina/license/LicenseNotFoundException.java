/*
 * Decompiled with CFR 0.152.
 */
package com.websina.license;

public class LicenseNotFoundException
extends Exception {
    private static final String MSG = "License Not Found";

    public LicenseNotFoundException(String msg) {
        super(msg);
    }

    public LicenseNotFoundException() {
        super(MSG);
    }
}

