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
@ToString(exclude = "vehicle")
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


    private double cost;

    private String serviceProvider;


    public MaintenanceRecord(LocalDate date, MaintenanceType type, String description, double cost, String serviceProvider) {
        this.date = date;
        this.maintenanceType = type;
        this.description = description;
        this.cost = cost;
        this.serviceProvider = serviceProvider;
    }
}