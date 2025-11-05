package com.FTMS.FTMS_app.shipment.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CargoDto {
    @NotEmpty
    private String description;
    @Min(1)
    private double weightKg;
    @Min(1)
    private double volumeCubicMeters;
    private String specialHandlingRequirements;
    private String additionalNotes;
}