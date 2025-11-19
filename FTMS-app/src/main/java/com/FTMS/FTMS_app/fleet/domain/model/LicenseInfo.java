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
@ToString
@EqualsAndHashCode
@Builder
public class LicenseInfo {

    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    private LicenseType licenseType;

    private LocalDate issueDate;
    private LocalDate expiryDate;


    public boolean isValid() {
        if (expiryDate == null) {
            return false;
        }
        return LocalDate.now().isBefore(expiryDate);
    }
}