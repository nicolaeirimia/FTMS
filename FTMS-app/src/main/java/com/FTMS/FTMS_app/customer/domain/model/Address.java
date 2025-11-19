package com.FTMS.FTMS_app.customer.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder            // <-- Util pentru teste și DTO mapping
@ToString           // <-- Util pentru debug
@EqualsAndHashCode  // <-- CRITIC pentru Value Objects
public class Address {

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    private String state; // Poate fi opțional în unele țări

    @NotBlank(message = "Zip code is required")
    private String zipCode;

    @NotBlank(message = "Country is required")
    private String country;

    // Metoda de formatare îmbunătățită
    public String getFullAddress() {
        // Folosim un format standard: Strada, Oraș, Județ (dacă există) Cod, Țara
        StringBuilder sb = new StringBuilder();
        sb.append(street).append(", ").append(city);

        if (state != null && !state.isEmpty()) {
            sb.append(", ").append(state);
        }

        sb.append(" ").append(zipCode).append(", ").append(country);
        return sb.toString();
    }
}