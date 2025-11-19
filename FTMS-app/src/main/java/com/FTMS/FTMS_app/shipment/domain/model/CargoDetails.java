package com.FTMS.FTMS_app.shipment.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class CargoDetails {

    @NotBlank(message = "Description is required")
    private String description;

    @Min(value = 0, message = "Weight cannot be negative")
    private double weightKg;

    @Min(value = 0, message = "Volume cannot be negative")
    private double volumeCubicMeters;

    private String specialHandlingRequirements;
    private String additionalNotes;
}