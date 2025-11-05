package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MaintenanceRecord> maintenanceHistory = new ArrayList<>();

    // --- Logica de Business ---

    /**
     * Verifică dacă vehiculul este disponibil pentru o cursă.
     */
    public boolean isAvailable() {
        return this.status == VehicleStatus.AVAILABLE &&
                !insuranceExpiryDate.isBefore(LocalDate.now()) &&
                !registrationExpiryDate.isBefore(LocalDate.now());
    }

    /**
     * Programează vehiculul pentru mentenanță.
     */
    public void scheduleMaintenance() {
        if (this.status == VehicleStatus.IN_USE) {
            throw new IllegalStateException("Cannot schedule maintenance, vehicle is currently in use.");
        }
        this.status = VehicleStatus.IN_MAINTENANCE;
    }

    /**
     * Marchează mentenanța ca fiind completă și adaugă o înregistrare.
     */
    public void completeMaintenance(MaintenanceRecord record) {
        if (this.status != VehicleStatus.IN_MAINTENANCE) {
            throw new IllegalStateException("Vehicle is not in maintenance.");
        }

        // Setează relația bidirecțională
        record.setVehicle(this);
        this.maintenanceHistory.add(record);
        this.status = VehicleStatus.AVAILABLE;
    }

    /**
     * Alocă vehiculul unei curse.
     */
    public void assignToShipment() {
        if (!isAvailable()) {
            throw new IllegalStateException("Vehicle " + registrationNumber + " is not available for assignment.");
        }
        this.status = VehicleStatus.IN_USE;
    }

    /**
     * Eliberează vehiculul după finalizarea cursei.
     */
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