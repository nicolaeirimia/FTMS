package com.FTMS.FTMS_app.shipment.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent; // <-- Import necesar
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryConfirmationDto {

    @NotNull(message = "Delivery time is required")
    @PastOrPresent(message = "Delivery time cannot be in the future") // <-- Validare logică
    private LocalDateTime actualDeliveryDateTime;

    @NotEmpty(message = "Recipient name is required")
    private String recipientName;

    private String recipientSignature;

    private String issuesOrDamages;

    private String photoDocumentationUrl;
}