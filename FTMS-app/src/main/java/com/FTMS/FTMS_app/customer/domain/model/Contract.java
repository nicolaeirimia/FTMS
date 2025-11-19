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
@Builder
@ToString(exclude = "customer")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ServiceLevel serviceLevel;

    private double discountRate;



    public boolean isActive() {
        LocalDate now = LocalDate.now();

        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    public boolean isNearingExpiry(int daysBeforeExpiry) {
        LocalDate expiryWarningDate = endDate.minusDays(daysBeforeExpiry);

        return LocalDate.now().isAfter(expiryWarningDate) && !LocalDate.now().isAfter(endDate);
    }
}