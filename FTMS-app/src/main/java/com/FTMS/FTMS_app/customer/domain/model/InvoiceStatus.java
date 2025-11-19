package com.FTMS.FTMS_app.customer.domain.model;

import lombok.Getter;

@Getter
public enum InvoiceStatus {
    PENDING("Pending Payment"),
    PARTIALLY_PAID("Partially Paid"),
    OVERDUE("Overdue"),
    PAID("Paid in Full");

    private final String displayName;

    InvoiceStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Verifică dacă mai sunt bani de recuperat pe această factură.
     * Util pentru a bloca comenzi noi (limita de credit).
     */
    public boolean isOutstanding() {
        return this == PENDING || this == OVERDUE || this == PARTIALLY_PAID;
    }
}