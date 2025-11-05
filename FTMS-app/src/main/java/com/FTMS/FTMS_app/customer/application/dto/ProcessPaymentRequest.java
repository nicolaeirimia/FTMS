package com.FTMS.FTMS_app.customer.application.dto;

import com.FTMS.FTMS_app.customer.domain.model.PaymentDetails;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProcessPaymentRequest {

    @NotNull
    private LocalDate paymentDate;

    @Min(1)
    private double amount;

    @NotNull
    private PaymentDetails.PaymentMethod paymentMethod;

    @NotEmpty
    private String referenceNumber;
}