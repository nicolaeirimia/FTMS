package com.FTMS.FTMS_app.customer.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter; // <-- ACEASTA LIPSEA CEL MAI PROBABIL
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Getter // <-- ASIGURĂ-TE CĂ ACEASTĂ LINIE EXISTĂ
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails {

    public enum PaymentMethod {
        BANK_TRANSFER,
        CREDIT_CARD,
        CASH
    }

    private LocalDate paymentDate;
    private double amount; // @Getter va crea automat getAmount() pentru acest câmp

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String referenceNumber;
}