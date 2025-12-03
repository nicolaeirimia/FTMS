package com.FTMS.FTMS_app.shipment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // <-- IMPORT NECESAR
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryConfirmationDto {

    @NotNull(message = "Delivery time is required")
    @PastOrPresent(message = "Delivery time cannot be in the future")
    @JsonProperty("actual_delivery_date_time")
    private LocalDateTime actualDeliveryDateTime;

    @NotEmpty(message = "Recipient name is required")
    @JsonProperty("recipient_name")
    private String recipientName;

    @JsonProperty("recipient_signature")
    private String recipientSignature;

    @JsonProperty("issues_or_damages")
    private String issuesOrDamages;

    @JsonProperty("photo_documentation_url")
    private String photoDocumentationUrl;
}