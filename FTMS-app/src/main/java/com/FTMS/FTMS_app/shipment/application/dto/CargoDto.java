package com.FTMS.FTMS_app.shipment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // <-- IMPORT NECESAR
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargoDto {

    @NotEmpty(message = "Cargo description is required")

    private String description;

    @Min(value = 1, message = "Weight must be at least 1 kg")
    @JsonProperty("weight_kg")
    private double weightKg;

    @Min(value = 1, message = "Volume must be at least 1 cubic meter")
    @JsonProperty("volume_m3") //
    private double volumeCubicMeters;

    @JsonProperty("special_handling_requirements")
    private String specialHandlingRequirements;

    @JsonProperty("additional_notes")
    private String additionalNotes;
}