package com.FTMS.FTMS_app.shipment.application.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ShipmentLocationDto {
    @NotEmpty
    private String street;
    @NotEmpty
    private String city;
    private String zipCode;
    private String country;
    @NotEmpty
    private String contactPerson;
    @NotEmpty
    private String contactPhone;
}