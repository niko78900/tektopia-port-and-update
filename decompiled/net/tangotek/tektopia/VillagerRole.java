/*
 * Decompiled with CFR 0.152.
 */
package net.tangotek.tektopia;

public enum VillagerRole {
    VILLAGER(1),
    DEFENDER(2),
    VENDOR(4),
    ENEMY(8),
    VISITOR(16);

    public final int value;

    private VillagerRole(int v) {
        this.value = v;
    }
}

