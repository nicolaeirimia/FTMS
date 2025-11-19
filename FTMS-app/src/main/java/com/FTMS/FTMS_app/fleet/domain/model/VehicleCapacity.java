package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min; // Necesită spring-boot-starter-validation
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class VehicleCapacity {

    @Min(value = 0, message = "Weight capacity cannot be negative")
    private double maxWeightKg;

    @Min(value = 0, message = "Volume capacity cannot be negative")
    private double maxVolumeCubicMeters;


    public boolean isSufficient(double cargoWeight, double cargoVolume) {

        return cargoWeight <= maxWeightKg && cargoVolume <= maxVolumeCubicMeters;
    }
}