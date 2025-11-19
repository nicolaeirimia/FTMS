package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*; // Am adăugat EqualsAndHashCode și ToString

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString           // Util pentru debug (vezi datele licenței în loguri)
@EqualsAndHashCode  // Esențial pentru Value Objects
@Builder            // Opțional, dar ajută la teste
public class LicenseInfo {

    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    private LicenseType licenseType;

    private LocalDate issueDate;
    private LocalDate expiryDate;

    /**
     * Verifică dacă licența este validă.
     * Returnează false dacă data de expirare lipsește.
     */
    public boolean isValid() {
        if (expiryDate == null) {
            return false; // Sau true, depinde de regula ta de business (dar evită NPE)
        }
        return LocalDate.now().isBefore(expiryDate); // Sau !isAfter() dacă vrei să incluzi și ziua de azi
    }
}