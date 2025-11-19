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


    public boolean isOutstanding() {
        return this == PENDING || this == OVERDUE || this == PARTIALLY_PAID;
    }
}