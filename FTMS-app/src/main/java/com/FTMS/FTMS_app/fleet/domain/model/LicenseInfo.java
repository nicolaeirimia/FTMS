package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable // Îi spune JPA că această clasă va fi "încorporată" în altă entitate
@Getter
@NoArgsConstructor // JPA are nevoie de un constructor fără argumente
@AllArgsConstructor
public class LicenseInfo {

    private String licenseNumber;

    @Enumerated(EnumType.STRING) // Salvează enum-ul ca text (ex: "CE") în loc de număr (ex: 1)
    private LicenseType licenseType;

    private LocalDate issueDate;
    private LocalDate expiryDate;

    // Poti adauga aici logica de business, ex:
    public boolean isValid() {
        return LocalDate.now().isBefore(expiryDate);
    }
}