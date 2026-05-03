package net.tangotek.tektopia.common;

public enum TekWorkerStatus {
    IDLE("idle"),
    MOVING("moving"),
    WORKING("working"),
    DELIVERING("delivering"),
    WAITING_FOR_INPUTS("waiting_for_inputs"),
    WAITING_FOR_STORAGE("waiting_for_storage"),
    BLOCKED("blocked"),
    COMBAT("combat"),
    RESTING("resting"),
    SOCIALIZING("socializing"),
    SLEEPING("sleeping"),
    VENDING("vending");

    private final String serializedName;

    TekWorkerStatus(String serializedName) {
        this.serializedName = serializedName;
    }

    public String getSerializedName() {
        return this.serializedName;
    }

    public static TekWorkerStatus fromSerializedName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return IDLE;
        }
        for (TekWorkerStatus status : values()) {
            if (status.serializedName.equals(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return IDLE;
    }
}
