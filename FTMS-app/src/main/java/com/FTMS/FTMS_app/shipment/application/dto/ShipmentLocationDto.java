package com.FTMS.FTMS_app.shipment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // <-- IMPORT NECESAR
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentLocationDto {

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Zip code is required")
    @JsonProperty("zip_code")
    private String zipCode;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Contact person is required")
    @JsonProperty("contact_person")
    private String contactPerson;

    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
    @JsonProperty("contact_phone")
    private String contactPhone;
}