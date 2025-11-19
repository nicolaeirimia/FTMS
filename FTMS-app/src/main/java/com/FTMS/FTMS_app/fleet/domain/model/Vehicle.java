package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.*;
import lombok.*; // Builder, ToString

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // <-- FOARTE UTIL pentru teste (CreateVehicleRequest -> Vehicle)
@ToString(exclude = "maintenanceHistory") // <-- CRITIC: Rupe bucla infinită cu MaintenanceRecord
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    private String make;
    private String model;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private int yearOfManufacture;

    @Embedded
    private VehicleCapacity capacity;

    private String fuelType;
    private double currentMileage;

    private String insurancePolicyNumber;
    private LocalDate insuranceExpiryDate;
    private LocalDate registrationExpiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Builder.Default // Necesar dacă folosim @Builder, ca să nu fie null lista
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MaintenanceRecord> maintenanceHistory = new ArrayList<>();

    // --- Logica de Business (Rămâne neschimbată - e perfectă) ---

    public boolean isAvailable() {
        return this.status == VehicleStatus.AVAILABLE &&
                !insuranceExpiryDate.isBefore(LocalDate.now()) &&
                !registrationExpiryDate.isBefore(LocalDate.now());
    }

    public void scheduleMaintenance() {
        if (this.status == VehicleStatus.IN_USE) {
            throw new IllegalStateException("Cannot schedule maintenance, vehicle is currently in use.");
        }
        this.status = VehicleStatus.IN_MAINTENANCE;
    }

    public void completeMaintenance(MaintenanceRecord record) {
        if (this.status != VehicleStatus.IN_MAINTENANCE) {
            throw new IllegalStateException("Vehicle is not in maintenance.");
        }
        record.setVehicle(this); // Legătura critică
        this.maintenanceHistory.add(record);
        this.status = VehicleStatus.AVAILABLE;
    }

    public void assignToShipment() {
        if (!isAvailable()) {
            throw new IllegalStateException("Vehicle " + registrationNumber + " is not available for assignment.");
        }
        this.status = VehicleStatus.IN_USE;
    }

    public void releaseFromShipment() {
        if (this.status == VehicleStatus.IN_USE) {
            this.status = VehicleStatus.AVAILABLE;
        }
    }

    public void updateMileage(double newMileage) {
        if (newMileage > this.currentMileage) {
            this.currentMileage = newMileage;
        }
    }
}