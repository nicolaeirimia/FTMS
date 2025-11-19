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
@Builder            // <-- Util pentru a crea obiectul în CustomerServiceImpl
@ToString           // <-- Util pentru debug
@EqualsAndHashCode  // <-- Critic pentru Value Objects
public class PaymentDetails {

    @NotNull
    private LocalDate paymentDate;

    @Min(value = 0, message = "Payment amount must be positive")
    private double amount;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod paymentMethod; // Folosește enum-ul extras

    private String referenceNumber;
}