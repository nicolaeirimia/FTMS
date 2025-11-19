package com.FTMS.FTMS_app.fleet.domain.model;

import lombok.Getter;

@Getter
public enum DriverStatus {
    AVAILABLE("Available"),
    ON_ROUTE("On Route"),
    ON_LEAVE("On Leave"),
    OFF_DUTY("Off Duty");

    private final String displayName;

    DriverStatus(String displayName) {
        this.displayName = displayName;
    }
}