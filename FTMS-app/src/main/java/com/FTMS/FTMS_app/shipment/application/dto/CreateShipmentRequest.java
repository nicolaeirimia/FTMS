package com.FTMS.FTMS_app.shipment.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateShipmentRequest {

    @NotEmpty
    private String referenceNumber;

    @NotNull
    private Long customerId;

    @NotNull
    @Valid // Asigură validarea obiectului interior
    private ShipmentLocationDto pickupLocation;

    @NotNull
    @Valid
    private ShipmentLocationDto deliveryLocation;

    @NotNull
    @Valid
    private CargoDto cargoDetails;

    @NotNull
    @Future(message = "Pickup time must be in the future.")
    private LocalDateTime pickupDateTime;

    @NotNull
    @Future(message = "Delivery time must be in the future.")
    private LocalDateTime requestedDeliveryDateTime;

    // Adăugăm un câmp pentru prețul cursei, necesar pentru facturare
    @Min(1)
    private double price;
}