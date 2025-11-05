package com.FTMS.FTMS_app.shipment.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryConfirmationDto {
    @NotNull
    private LocalDateTime actualDeliveryDateTime;
    @NotEmpty
    private String recipientName;
    private String recipientSignature; // Poate fi un URL către o imagine
    private String issuesOrDamages;
    private String photoDocumentationUrl;
}