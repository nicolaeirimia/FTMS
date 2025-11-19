package com.FTMS.FTMS_app.shipment.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder            // Util pentru teste și mapping
@ToString           // Util pentru debug
@EqualsAndHashCode  // Critic pentru Value Objects
public class ShipmentContactLocation {

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Zip code is required")
    private String zipCode;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Contact person is required")
    private String contactPerson;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    /**
     * Formatează adresa completă pentru afișare în UI sau pe factură.
     */
    public String getFullAddress() {
        return String.format("%s, %s, %s, %s (Contact: %s, %s)",
                street, city, zipCode, country, contactPerson, contactPhone);
    }
}