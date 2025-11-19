package com.FTMS.FTMS_app.shipment.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent; // Alternativă mai flexibilă
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateShipmentRequest {

    @NotEmpty(message = "Reference number is required")
    private String referenceNumber;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Pickup location is required")
    @Valid
    private ShipmentLocationDto pickupLocation;

    @NotNull(message = "Delivery location is required")
    @Valid
    private ShipmentLocationDto deliveryLocation;

    @NotNull(message = "Cargo details are required")
    @Valid
    private CargoDto cargoDetails;

    @NotNull(message = "Pickup time is required")
    @FutureOrPresent(message = "Pickup time must be in the present or future")
    private LocalDateTime pickupDateTime;

    @NotNull(message = "Delivery time is required")
    @FutureOrPresent(message = "Delivery time must be in the present or future")
    private LocalDateTime requestedDeliveryDateTime;

    @Min(value = 1, message = "Price must be positive")
    private double price;
}