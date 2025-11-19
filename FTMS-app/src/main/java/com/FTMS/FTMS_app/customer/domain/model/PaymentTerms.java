package com.FTMS.FTMS_app.customer.domain.model;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public enum PaymentTerms {
    PREPAID(0, "Prepaid"),
    NET_15(15, "Net 15 Days"),
    NET_30(30, "Net 30 Days"),
    NET_60(60, "Net 60 Days");

    private final int days;
    private final String displayName;

    PaymentTerms(int days, String displayName) {
        this.days = days;
        this.displayName = displayName;
    }

    /**
     * Calculează data scadenței pe baza datei emiterii.
     * Această metodă elimină logica if/else din Service.
     */
    public LocalDate calculateDueDate(LocalDate issueDate) {
        return issueDate.plusDays(this.days);
    }
}