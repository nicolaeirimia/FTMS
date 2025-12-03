package com.FTMS.FTMS_app.shipment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // <-- IMPORT NECESAR
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateShipmentRequest {

    @NotEmpty(message = "Reference number is required")
    @JsonProperty("reference_number")
    private String referenceNumber;

    @NotNull(message = "Customer ID is required")
    @JsonProperty("customer_id")
    private Long customerId;

    @NotNull(message = "Pickup location is required")
    @Valid
    @JsonProperty("pickup_location")
    private ShipmentLocationDto pickupLocation;

    @NotNull(message = "Delivery location is required")
    @Valid
    @JsonProperty("delivery_location")
    private ShipmentLocationDto deliveryLocation;

    @NotNull(message = "Cargo details are required")
    @Valid
    @JsonProperty("cargo_details")
    private CargoDto cargoDetails;

    @NotNull(message = "Pickup time is required")
    @FutureOrPresent(message = "Pickup time must be in the present or future")
    @JsonProperty("pickup_date_time")
    private LocalDateTime pickupDateTime;

    @NotNull(message = "Delivery time is required")
    @FutureOrPresent(message = "Delivery time must be in the present or future")
    @JsonProperty("requested_delivery_date_time")
    private LocalDateTime requestedDeliveryDateTime;

    @Min(value = 1, message = "Price must be positive")
    @JsonProperty("price")
    private double price;
}