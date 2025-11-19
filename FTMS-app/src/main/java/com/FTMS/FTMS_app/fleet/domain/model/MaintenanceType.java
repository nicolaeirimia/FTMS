package com.FTMS.FTMS_app.fleet.domain.model;

import lombok.Getter;

@Getter
public enum MaintenanceType {
    ROUTINE_SERVICE("Routine Service"),
    REPAIR("Repair / Fix"),
    SAFETY_INSPECTION("Safety Inspection"),
    COMPLIANCE_CHECK("Regulatory Compliance Check");

    private final String displayName;

    MaintenanceType(String displayName) {
        this.displayName = displayName;
    }
}