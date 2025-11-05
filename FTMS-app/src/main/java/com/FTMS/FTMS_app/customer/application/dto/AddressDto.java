package com.FTMS.FTMS_app.customer.application.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddressDto {
    @NotEmpty
    private String street;
    @NotEmpty
    private String city;
    private String state;
    @NotEmpty
    private String zipCode;
    private String country;
}