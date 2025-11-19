package com.FTMS.FTMS_app.shipment.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargoDto {

    @NotEmpty(message = "Cargo description is required")
    private String description;

    @Min(value = 1, message = "Weight must be at least 1 kg")
    private double weightKg;

    @Min(value = 1, message = "Volume must be at least 1 cubic meter")
    private double volumeCubicMeters;

    private String specialHandlingRequirements;
    private String additionalNotes;
}