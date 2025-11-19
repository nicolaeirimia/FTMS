package com.FTMS.FTMS_app.customer.domain.model;

import lombok.Getter;

@Getter
public enum CustomerStatus {
    ACTIVE("Active Account"),
    SUSPENDED("Suspended (Payment Overdue)"),
    INACTIVE("Inactive / Closed");

    private final String displayName;

    CustomerStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Verifică dacă clientul are dreptul să plaseze comenzi noi.
     */
    public boolean canPlaceOrders() {
        return this == ACTIVE;
    }
}