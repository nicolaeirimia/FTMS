package com.FTMS.FTMS_app.fleet.domain.model;

import lombok.Getter;

@Getter
public enum VehicleStatus {
    AVAILABLE("Available"),
    IN_USE("In Use"),
    IN_MAINTENANCE("In Maintenance"),
    OUT_OF_SERVICE("Out of Service");

    private final String displayName;

    VehicleStatus(String displayName) {
        this.displayName = displayName;
    }
}