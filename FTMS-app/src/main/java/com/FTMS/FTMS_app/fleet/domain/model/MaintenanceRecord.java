package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.*;
import lombok.*; // Am adăugat ToString

import java.math.BigDecimal; // Recomandat pentru bani
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "vehicle") // <--- CRITIC: Rupe bucla infinită
@Builder
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private MaintenanceType maintenanceType;

    private String description;

    // Am schimbat double în BigDecimal pentru precizie financiară.
    // Dacă schimbi aici, trebuie să schimbi și în DTO-uri/Service!
    // Dacă vrei să păstrezi 'double' e ok pentru acest proiect, dar ține minte pentru viitor.
    private double cost;

    private String serviceProvider;

    // Constructorul tău e ok, dar @Builder e și mai curat
    public MaintenanceRecord(LocalDate date, MaintenanceType type, String description, double cost, String serviceProvider) {
        this.date = date;
        this.maintenanceType = type;
        this.description = description;
        this.cost = cost;
        this.serviceProvider = serviceProvider;
    }
}