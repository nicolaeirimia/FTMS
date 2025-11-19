package com.FTMS.FTMS_app.customer.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // <-- Util pentru teste/service
@ToString(exclude = "customer") // <-- CRITIC: Rupe bucla infinită
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // Performanță: Nu încărca clientul decât dacă e nevoie
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING) // <-- Folosim Enum pentru siguranță
    private ServiceLevel serviceLevel;

    private double discountRate; // 0.10 pentru 10%

    // --- Logica de Business (Perfectă) ---

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        // Logica ta este corectă: inclusive start, inclusive end
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    public boolean isNearingExpiry(int daysBeforeExpiry) {
        LocalDate expiryWarningDate = endDate.minusDays(daysBeforeExpiry);
        // Verificăm dacă suntem în intervalul de avertizare (după data de alertă, dar înainte de expirare)
        return LocalDate.now().isAfter(expiryWarningDate) && !LocalDate.now().isAfter(endDate);
    }
}