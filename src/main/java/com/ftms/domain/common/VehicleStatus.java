package com.ftms.domain.common;

/**
 * Vehicle availability statuses
 */
public enum VehicleStatus {
    AVAILABLE("Available"),
    IN_USE("In Use"),
    IN_MAINTENANCE("In Maintenance"),
    OUT_OF_SERVICE("Out of Service");

    private final String displayName;

    VehicleStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
