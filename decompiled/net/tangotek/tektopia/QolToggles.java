package net.tangotek.tektopia;

public final class QolToggles {
    private static final String PREFIX = "tektopia.qol.";

    private QolToggles() {
    }

    public static boolean prioritizeCraftNeed() {
        return getBoolean("craft.prioritize_need", true);
    }

    public static boolean enforceCraftPersonalLimit() {
        return getBoolean("craft.enforce_personal_limit", true);
    }

    private static boolean getBoolean(String key, boolean fallback) {
        String value = System.getProperty(PREFIX + key);
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim().toLowerCase();
        return normalized.equals("1") || normalized.equals("true") || normalized.equals("yes") || normalized.equals("on");
    }
}
