package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "maintenance_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false) // Partea "Many" a relației
    private Vehicle vehicle;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private MaintenanceType maintenanceType;

    private String description;
    private double cost;
    private String serviceProvider;

    // Constructor util pentru a crea o înregistrare fără ID (ID-ul va fi generat de JPA)
    public MaintenanceRecord(LocalDate date, MaintenanceType type, String description, double cost, String serviceProvider) {
        this.date = date;
        this.maintenanceType = type;
        this.description = description;
        this.cost = cost;
        this.serviceProvider = serviceProvider;
    }
}