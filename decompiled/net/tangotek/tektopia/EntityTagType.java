/*
 * Decompiled with CFR 0.152.
 */
package net.tangotek.tektopia;

public enum EntityTagType {
    VILLAGER("villager", "\u00c2\u00a72"),
    BUTCHERED("butchered", "\u00c2\u00a71");

    public final String tag;
    public final String colorPrefx;

    private EntityTagType(String tag, String colorPrefix) {
        this.tag = tag;
        this.colorPrefx = colorPrefix;
    }
}

