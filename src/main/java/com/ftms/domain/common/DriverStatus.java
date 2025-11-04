package com.ftms.domain.common;

/**
 * Driver availability statuses
 */
public enum DriverStatus {
    AVAILABLE("Available"),
    ON_ROUTE("On Route"),
    ON_LEAVE("On Leave"),
    OFF_DUTY("Off Duty");

    private final String displayName;

    DriverStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
