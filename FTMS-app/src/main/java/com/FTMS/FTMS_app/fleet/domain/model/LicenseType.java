package com.FTMS.FTMS_app.fleet.domain.model;

import lombok.Getter;

@Getter
public enum LicenseType {
    C("Vehicul de marfă > 3.5t"),
    CE("Vehicul C + Remorcă grea");

    private final String description;

    LicenseType(String description) {
        this.description = description;
    }

    /**
     * Verifică dacă acest permis îl acoperă pe cel cerut.
     * Ex: CE.covers(C) -> true
     * C.covers(CE) -> false
     */
    public boolean covers(LicenseType required) {
        if (this == required) return true;
        // CE este "superior" lui C
        return this == CE && required == C;
    }
}