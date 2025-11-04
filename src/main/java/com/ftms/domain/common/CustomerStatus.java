package com.ftms.domain.common;

/**
 * Customer account statuses
 */
public enum CustomerStatus {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    INACTIVE("Inactive");

    private final String displayName;

    CustomerStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
