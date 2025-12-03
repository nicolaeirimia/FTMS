package com.FTMS.FTMS_app.shipment.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference; // <-- IMPORT NOU
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "deliveryConfirmation")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    // --- ID-uri de referință (Loose Coupling) ---
    @Column(nullable = false)
    private Long customerId;

    private Long assignedDriverId;
    private Long assignedVehicleId;

    // --- Value Objects ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "pickup_street")),
            @AttributeOverride(name = "city", column = @Column(name = "pickup_city")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "pickup_zip")),
            @AttributeOverride(name = "country", column = @Column(name = "pickup_country")),
            @AttributeOverride(name = "contactPerson", column = @Column(name = "pickup_contact_person")),
            @AttributeOverride(name = "contactPhone", column = @Column(name = "pickup_contact_phone"))
    })
    private ShipmentContactLocation pickupLocation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "delivery_street")),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_city")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "delivery_zip")),
            @AttributeOverride(name = "country", column = @Column(name = "delivery_country")),
            @AttributeOverride(name = "contactPerson", column = @Column(name = "delivery_contact_person")),
            @AttributeOverride(name = "contactPhone", column = @Column(name = "delivery_contact_phone"))
    })
    private ShipmentContactLocation deliveryLocation;

    @Embedded
    private CargoDetails cargoDetails;

    private LocalDateTime pickupDateTime;
    private LocalDateTime requestedDeliveryDateTime;

    // --- Relația Bidirecțională ---
    @OneToOne(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // <-- CRITIC: Permite serializarea confirmării, dar gestionează bucla
    private DeliveryConfirmation deliveryConfirmation;

    // --- Logica de Business ---

    public boolean canBeAssigned() {
        return this.status == ShipmentStatus.PENDING || this.status == ShipmentStatus.SCHEDULED;
    }

    public void assign(Long driverId, Long vehicleId) {
        if (!canBeAssigned()) {
            throw new IllegalStateException("Shipment cannot be assigned in its current state: " + this.status);
        }
        this.assignedDriverId = driverId;
        this.assignedVehicleId = vehicleId;
        this.status = ShipmentStatus.SCHEDULED;
    }

    public void markAsPickedUp() {
        if (this.status != ShipmentStatus.SCHEDULED) {
            throw new IllegalStateException("Shipment must be SCHEDULED to be picked up.");
        }
        this.status = ShipmentStatus.PICKED_UP;
    }

    public void markAsInTransit() {
        if (this.status != ShipmentStatus.PICKED_UP) {
            throw new IllegalStateException("Shipment must be PICKED_UP to be in transit.");
        }
        this.status = ShipmentStatus.IN_TRANSIT;
    }

    public void cancel() {
        if (this.status == ShipmentStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered shipment.");
        }
        this.status = ShipmentStatus.CANCELED;
    }

    public void completeDelivery(DeliveryConfirmation confirmation) {
        if (this.status != ShipmentStatus.IN_TRANSIT) {
            throw new IllegalStateException("Shipment must be IN_TRANSIT to be delivered.");
        }
        this.deliveryConfirmation = confirmation;

        if (confirmation.getShipment() == null) {
            confirmation.setShipment(this);
        }
        this.status = ShipmentStatus.DELIVERED;
    }
}