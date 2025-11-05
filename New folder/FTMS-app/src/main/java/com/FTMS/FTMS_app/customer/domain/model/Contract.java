package com.FTMS.FTMS_app.customer.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne // Relația inversă
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private LocalDate startDate;
    private LocalDate endDate;

    // Aici ar putea fi definite mai multe reguli
    private String serviceLevel; // "standard", "express"
    private double discountRate; // ex: 0.10 pentru 10%

    // --- Logica de Business ---

    /**
     * Verifică dacă contractul este activ în prezent.
     */
    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    /**
     * Verifică dacă contractul este pe cale să expire (ex: în 30 de zile).
     */
    public boolean isNearingExpiry(int daysBeforeExpiry) {
        LocalDate expiryWarningDate = endDate.minusDays(daysBeforeExpiry);
        return LocalDate.now().isAfter(expiryWarningDate) && !LocalDate.now().isAfter(endDate);
    }
}