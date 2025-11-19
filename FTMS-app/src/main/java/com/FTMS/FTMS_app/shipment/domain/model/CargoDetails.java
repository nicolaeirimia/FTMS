package com.FTMS.FTMS_app.shipment.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder            // <-- Util pentru a construi obiectul clar
@ToString           // <-- Util pentru debug
@EqualsAndHashCode  // <-- CRITIC pentru Value Objects (compară valorile, nu referința)
public class CargoDetails {

    @NotBlank(message = "Description is required")
    private String description;

    @Min(value = 0, message = "Weight cannot be negative")
    private double weightKg;

    @Min(value = 0, message = "Volume cannot be negative")
    private double volumeCubicMeters;

    private String specialHandlingRequirements; // Poate fi null/gol
    private String additionalNotes;             // Poate fi null/gol
}