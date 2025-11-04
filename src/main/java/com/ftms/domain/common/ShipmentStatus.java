package com.ftms.domain.common;

/**
 * Shipment lifecycle statuses
 */
public enum ShipmentStatus {
    PENDING("Pending"),
    SCHEDULED("Scheduled"),
    PICKED_UP("Picked Up"),
    IN_TRANSIT("In Transit"),
    DELIVERED("Delivered"),
    CANCELED("Canceled");

    private final String displayName;

    ShipmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
