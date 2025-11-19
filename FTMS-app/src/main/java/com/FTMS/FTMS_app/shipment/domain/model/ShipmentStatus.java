package com.FTMS.FTMS_app.shipment.domain.model;

import lombok.Getter;

@Getter
public enum ShipmentStatus {
    PENDING("Pending Approval"),
    SCHEDULED("Scheduled & Assigned"),
    PICKED_UP("Picked Up"),
    IN_TRANSIT("In Transit"),
    DELIVERED("Delivered Successfully"),
    CANCELED("Canceled");

    private final String displayName;

    ShipmentStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Verifică dacă este permisă trecerea la noul status.
     * (Logica de State Machine)
     */
    public boolean canTransitionTo(ShipmentStatus newStatus) {
        if (this == newStatus) return true;
        if (this == CANCELED || this == DELIVERED) return false; // Stări finale

        return switch (this) {
            case PENDING -> newStatus == SCHEDULED || newStatus == CANCELED;
            case SCHEDULED -> newStatus == PICKED_UP || newStatus == CANCELED;
            case PICKED_UP -> newStatus == IN_TRANSIT;
            case IN_TRANSIT -> newStatus == DELIVERED;
            default -> false;
        };
    }
}