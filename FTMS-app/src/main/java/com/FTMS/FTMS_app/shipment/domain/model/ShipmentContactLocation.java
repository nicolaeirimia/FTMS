package com.FTMS.FTMS_app.shipment.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Acest VO *nu* extinde Address. Este un VO specific acestui modul
// pentru a stoca adresa plată + contactul.

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentContactLocation {

    // Am putea folosi un VO Address, dar pentru a simplifica
    // și a evita dependențe între domenii, folosim câmpuri simple
    private String street;
    private String city;
    private String zipCode;
    private String country;

    private String contactPerson;
    private String contactPhone;

    public String getFullAddress() {
        return street + ", " + city + " " + zipCode;
    }
}