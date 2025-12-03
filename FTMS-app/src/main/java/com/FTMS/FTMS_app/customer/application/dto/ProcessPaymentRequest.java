package com.FTMS.FTMS_app.customer.application.dto;

import com.FTMS.FTMS_app.customer.domain.model.PaymentMethod; // <-- IMPORTANT: Importul corect
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@Data
public class ProcessPaymentRequest {

    @NotNull
    private LocalDate paymentDate;

    @Min(1)
    private double amount;

    @NotNull
    @JsonProperty("payment_Method")
    private PaymentMethod paymentMethod;

    @NotEmpty
    @JsonProperty("referenceNumber")
    private String referenceNumber;
}