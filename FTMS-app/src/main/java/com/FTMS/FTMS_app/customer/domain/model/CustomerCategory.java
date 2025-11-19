package com.FTMS.FTMS_app.customer.domain.model;

import lombok.Getter;

@Getter
public enum CustomerCategory {
    STANDARD("Standard", 0.00, 1),
    PREMIUM("Premium Partner", 0.10, 2), // 10% discount
    VIP("VIP Global Account", 0.20, 3);  // 20% discount

    private final String displayName;
    private final double discountPercentage;
    private final int priorityLevel;

    CustomerCategory(String displayName, double discountPercentage, int priorityLevel) {
        this.displayName = displayName;
        this.discountPercentage = discountPercentage;
        this.priorityLevel = priorityLevel;
    }



    public double applyDiscount(double originalPrice) {
        return originalPrice - (originalPrice * this.discountPercentage);
    }
}