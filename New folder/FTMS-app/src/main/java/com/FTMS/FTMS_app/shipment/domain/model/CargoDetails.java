package com.FTMS.FTMS_app.shipment.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CargoDetails {

    private String description;
    private double weightKg;
    private double volumeCubicMeters;
    private String specialHandlingRequirements;
    private String additionalNotes;
}