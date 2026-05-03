package net.tangotek.tektopia.common;

import java.util.Locale;

public enum ProfessionType {
    BARD("bard", true),
    BLACKSMITH("blacksmith", true),
    BUTCHER("butcher", true),
    CHEF("chef", true),
    CLERIC("cleric", true),
    DRUID("druid", true),
    ENCHANTER("enchanter", true),
    FARMER("farmer", true),
    GUARD("guard", true),
    CAPTAIN("captain", true),
    LUMBERJACK("lumberjack", true),
    MINER("miner", true),
    RANCHER("rancher", true),
    TEACHER("teacher", true),
    CHILD("child", false),
    NITWIT("nitwit", false),
    NOMAD("nomad", false),
    MERCHANT("merchant", false),
    UNKNOWN("unknown", false);

    private final String serializedName;
    private final boolean copyableSkill;

    ProfessionType(String serializedName, boolean copyableSkill) {
        this.serializedName = serializedName;
        this.copyableSkill = copyableSkill;
    }

    public String getSerializedName() {
        return this.serializedName;
    }

    public boolean isCopyableSkill() {
        return this.copyableSkill;
    }

    public static ProfessionType fromSerializedName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return UNKNOWN;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (ProfessionType professionType : values()) {
            if (professionType.serializedName.equals(normalized) || professionType.name().toLowerCase(Locale.ROOT).equals(normalized)) {
                return professionType;
            }
        }
        return UNKNOWN;
    }
}
