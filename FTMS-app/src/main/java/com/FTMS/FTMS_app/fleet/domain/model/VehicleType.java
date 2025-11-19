package com.FTMS.FTMS_app.fleet.domain.model;

import lombok.Getter;

@Getter
public enum VehicleType {
    BOX_TRUCK("Box Truck", false),
    FLATBED("Flatbed Truck", true),
    REFRIGERATED("Refrigerated Truck", false),
    TANKER("Tanker Truck", true);

    private final String displayName;
    private final boolean requiresHeavyLicense;

    VehicleType(String displayName, boolean requiresHeavyLicense) {
        this.displayName = displayName;
        this.requiresHeavyLicense = requiresHeavyLicense;
    }
}