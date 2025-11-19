package com.FTMS.FTMS_app.customer.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Address {

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    private String state;

    @NotBlank(message = "Zip code is required")
    private String zipCode;

    @NotBlank(message = "Country is required")
    private String country;


    public String getFullAddress() {

        StringBuilder sb = new StringBuilder();
        sb.append(street).append(", ").append(city);

        if (state != null && !state.isEmpty()) {
            sb.append(", ").append(state);
        }

        sb.append(" ").append(zipCode).append(", ").append(country);
        return sb.toString();
    }
}