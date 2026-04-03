/*
 * Decompiled with CFR 0.152.
 */
package net.tangotek.tektopia;

public enum ItemTagType {
    VILLAGER("villager", "\u00c2\u00a72"),
    STRUCTURE("struct", "");

    public final String tag;
    public final String colorPrefix;

    private ItemTagType(String tag, String colorPrefix) {
        this.tag = tag;
        this.colorPrefix = colorPrefix;
    }
}

