package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Numele șoferului

    @Embedded
    private LicenseInfo licenseInfo;

    // --- MODIFICARE AICI ---
    // Am adăugat @AttributeOverrides (plural) pentru a redenumi TOATE
    // câmpurile din ContactInfo, inclusiv 'address' și 'email'.
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "primary_contact_name")),
            @AttributeOverride(name = "phone", column = @Column(name = "primary_contact_phone")),
            @AttributeOverride(name = "email", column = @Column(name = "primary_contact_email")),
            @AttributeOverride(name = "address", column = @Column(name = "primary_contact_address"))
    })
    private ContactInfo contactDetails;

    // --- MODIFICARE AICI ---
    // Am completat @AttributeOverrides pentru a include și 'address' și 'email'.
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "emergency_contact_name")),
            @AttributeOverride(name = "phone", column = @Column(name = "emergency_contact_phone")),
            @AttributeOverride(name = "email", column = @Column(name = "emergency_contact_email")),
            @AttributeOverride(name = "address", column = @Column(name = "emergency_contact_address"))
    })
    private ContactInfo emergencyContact;

    private LocalDate employmentDate;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    @OneToOne
    @JoinColumn(name = "primary_vehicle_id") // Cheia străină va fi în tabelul drivers
    private Vehicle primaryVehicle;

    // --- Logica de Business (rămâne neschimbată) ---

    public boolean isAvailable() {
        return this.status == DriverStatus.AVAILABLE && licenseInfo.isValid();
    }

    public boolean canDriveVehicle(Vehicle vehicle) {
        return licenseInfo.getLicenseType() == LicenseType.CE ||
                (licenseInfo.getLicenseType() == LicenseType.C &&
                        (vehicle.getVehicleType() != VehicleType.TANKER && vehicle.getVehicleType() != VehicleType.FLATBED));
    }

    public void assignToShipment() {
        if (!isAvailable()) {
            throw new IllegalStateException("Driver " + name + " is not available for assignment.");
        }
        this.status = DriverStatus.ON_ROUTE;
    }

    public void completeShipment() {
        if (this.status == DriverStatus.ON_ROUTE) {
            this.status = DriverStatus.AVAILABLE;
        }
    }
}