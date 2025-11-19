package com.FTMS.FTMS_app.customer.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class PaymentDetails {

    @NotNull
    private LocalDate paymentDate;

    @Min(value = 0, message = "Payment amount must be positive")
    private double amount;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod paymentMethod;

    private String referenceNumber;
}