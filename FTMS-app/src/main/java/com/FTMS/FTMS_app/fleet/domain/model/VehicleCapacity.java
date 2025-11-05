package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCapacity {

    private double maxWeightKg;
    private double maxVolumeCubicMeters;

    /**
     * Logică de business pentru a verifica dacă încărcătura încape.
     */
    public boolean isSufficient(double cargoWeight, double cargoVolume) {
        return cargoWeight <= maxWeightKg && cargoVolume <= maxVolumeCubicMeters;
    }
}