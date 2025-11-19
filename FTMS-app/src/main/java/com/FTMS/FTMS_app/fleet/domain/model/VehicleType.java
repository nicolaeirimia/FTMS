package com.FTMS.FTMS_app.fleet.domain.model;

import lombok.Getter;

@Getter
public enum VehicleType {
    BOX_TRUCK("Box Truck", false),
    FLATBED("Flatbed Truck", true),      // Presupunem că cere permis special/experiență
    REFRIGERATED("Refrigerated Truck", false),
    TANKER("Tanker Truck", true);        // Presupunem că cere permis special (CE)

    private final String displayName;
    private final boolean requiresHeavyLicense; // Ex: cere neapărat CE

    VehicleType(String displayName, boolean requiresHeavyLicense) {
        this.displayName = displayName;
        this.requiresHeavyLicense = requiresHeavyLicense;
    }
}