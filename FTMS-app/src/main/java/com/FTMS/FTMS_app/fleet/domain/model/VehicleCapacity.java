package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min; // Necesită spring-boot-starter-validation
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString           // Util pentru loguri (vezi capacitatea direct)
@EqualsAndHashCode  // CRITIC: Face ca două capacități identice să fie "egale"
public class VehicleCapacity {

    @Min(value = 0, message = "Weight capacity cannot be negative")
    private double maxWeightKg;

    @Min(value = 0, message = "Volume capacity cannot be negative")
    private double maxVolumeCubicMeters;

    /**
     * Logică de business pentru a verifica dacă încărcătura încape.
     */
    public boolean isSufficient(double cargoWeight, double cargoVolume) {
        // Putem adăuga o marjă de eroare (epsilon) pentru double, dar simplu e ok
        return cargoWeight <= maxWeightKg && cargoVolume <= maxVolumeCubicMeters;
    }
}