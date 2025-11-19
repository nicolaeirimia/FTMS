package com.FTMS.FTMS_app.fleet.domain.model;

import jakarta.persistence.*;
import lombok.*; // Am adăugat Builder și ToString

import java.time.LocalDate;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // <-- Util pentru teste
@ToString(exclude = "primaryVehicle") // <-- Evită bucle infinite dacă Vehicle are referință înapoi la Driver
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // Validare DB: Numele e obligatoriu
    private String name;

    @Embedded
    private LicenseInfo licenseInfo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "primary_contact_name")),
            @AttributeOverride(name = "phone", column = @Column(name = "primary_contact_phone")),
            @AttributeOverride(name = "email", column = @Column(name = "primary_contact_email")),
            @AttributeOverride(name = "address", column = @Column(name = "primary_contact_address"))
    })
    private ContactInfo contactDetails;

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

    @OneToOne(fetch = FetchType.LAZY) // Performanță: Nu încărca vehiculul dacă nu e nevoie
    @JoinColumn(name = "primary_vehicle_id")
    private Vehicle primaryVehicle;

    // --- Logica de Business Îmbunătățită (Null Safety) ---

    public boolean isAvailable() {
        // Verificăm dacă licenseInfo există înainte să apelăm metode pe el
        return this.status == DriverStatus.AVAILABLE
                && licenseInfo != null
                && licenseInfo.isValid();
    }

    public boolean canDriveVehicle(Vehicle vehicle) {
        if (vehicle == null || licenseInfo == null) return false; // Safety check

        return licenseInfo.getLicenseType() == LicenseType.CE ||
                (licenseInfo.getLicenseType() == LicenseType.C &&
                        (vehicle.getVehicleType() != VehicleType.TANKER && vehicle.getVehicleType() != VehicleType.FLATBED));
    }

    // Restul metodelor rămân la fel
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