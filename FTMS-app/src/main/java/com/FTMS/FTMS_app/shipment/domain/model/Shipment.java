package com.FTMS.FTMS_app.shipment.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    // --- ID-uri de referință către alte agregate ---
    @Column(nullable = false)
    private Long customerId; // Referință la Customer

    private Long assignedDriverId; // Referință la Driver
    private Long assignedVehicleId; // Referință la Vehicle

    // --- Value Objects încorporate ---
    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "pickup_street"))
    @AttributeOverride(name = "city", column = @Column(name = "pickup_city"))
    @AttributeOverride(name = "zipCode", column = @Column(name = "pickup_zip"))
    @AttributeOverride(name = "country", column = @Column(name = "pickup_country"))
    @AttributeOverride(name = "contactPerson", column = @Column(name = "pickup_contact_person"))
    @AttributeOverride(name = "contactPhone", column = @Column(name = "pickup_contact_phone"))
    private ShipmentContactLocation pickupLocation;

    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "delivery_street"))
    @AttributeOverride(name = "city", column = @Column(name = "delivery_city"))
    @AttributeOverride(name = "zipCode", column = @Column(name = "delivery_zip"))
    @AttributeOverride(name = "country", column = @Column(name = "delivery_country"))
    @AttributeOverride(name = "contactPerson", column = @Column(name = "delivery_contact_person"))
    @AttributeOverride(name = "contactPhone", column = @Column(name = "delivery_contact_phone"))
    private ShipmentContactLocation deliveryLocation;

    @Embedded
    private CargoDetails cargoDetails;

    // --- Timpi ---
    private LocalDateTime pickupDateTime;
    private LocalDateTime requestedDeliveryDateTime;

    // --- Entitate componentă ---
    @OneToOne(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DeliveryConfirmation deliveryConfirmation;

    // Constructor pentru creare
    public Shipment(String referenceNumber, Long customerId, ShipmentContactLocation pickupLocation, ShipmentContactLocation deliveryLocation, CargoDetails cargoDetails, LocalDateTime pickupDateTime, LocalDateTime requestedDeliveryDateTime) {
        this.referenceNumber = referenceNumber;
        this.customerId = customerId;
        this.pickupLocation = pickupLocation;
        this.deliveryLocation = deliveryLocation;
        this.cargoDetails = cargoDetails;
        this.pickupDateTime = pickupDateTime;
        this.requestedDeliveryDateTime = requestedDeliveryDateTime;
        this.status = ShipmentStatus.PENDING; // Status inițial
    }

    // --- Logica de Business (Metode de domeniu) ---

    /**
     * Verifică dacă transportul poate fi alocat.
     */
    public boolean canBeAssigned() {
        return this.status == ShipmentStatus.PENDING || this.status == ShipmentStatus.SCHEDULED;
    }

    /**
     * Alocă un șofer și un vehicul.
     */
    public void assign(Long driverId, Long vehicleId) {
        if (!canBeAssigned()) {
            throw new IllegalStateException("Shipment cannot be assigned in its current state: " + this.status);
        }
        this.assignedDriverId = driverId;
        this.assignedVehicleId = vehicleId;
        this.status = ShipmentStatus.SCHEDULED;
    }

    /**
     * Marchează transportul ca fiind ridicat.
     */
    public void markAsPickedUp() {
        if (this.status != ShipmentStatus.SCHEDULED) {
            throw new IllegalStateException("Shipment must be SCHEDULED to be picked up.");
        }
        this.status = ShipmentStatus.PICKED_UP;
    }

    /**
     * Marchează transportul ca fiind în tranzit.
     */
    public void markAsInTransit() {
        if (this.status != ShipmentStatus.PICKED_UP) {
            throw new IllegalStateException("Shipment must be PICKED_UP to be in transit.");
        }
        this.status = ShipmentStatus.IN_TRANSIT;
    }

    /**
     * Anulează transportul.
     */
    public void cancel() {
        if (this.status == ShipmentStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered shipment.");
        }
        this.status = ShipmentStatus.CANCELED;
        // Eliberarea resurselor (driver/vehicle) va fi gestionată de Application Service
    }

    /**
     * Finalizează livrarea și atașează confirmarea.
     */
    public void completeDelivery(DeliveryConfirmation confirmation) {
        if (this.status != ShipmentStatus.IN_TRANSIT) {
            throw new IllegalStateException("Shipment must be IN_TRANSIT to be delivered.");
        }
        this.deliveryConfirmation = confirmation;
        // Asigură legătura bidirecțională
        if (confirmation.getShipment() == null) {
            confirmation.setShipment(this);
        }
        this.status = ShipmentStatus.DELIVERED;
    }
}