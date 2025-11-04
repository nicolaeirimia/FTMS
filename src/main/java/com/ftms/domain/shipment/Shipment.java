package com.ftms.domain.shipment;

import com.ftms.domain.common.Location;
import com.ftms.domain.common.ShipmentStatus;
import com.ftms.domain.fleet.Driver;
import com.ftms.domain.fleet.Vehicle;
import com.ftms.domain.customer.Customer;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.Objects;

/**
 * Shipment Entity - Aggregate Root
 * Manages the complete lifecycle of a freight shipment
 */
public class Shipment {
    
    // Identity
    private Integer shipmentId;
    
    @NotBlank(message = "Shipment reference number is required")
    private String referenceNumber;
    
    // Locations
    @NotNull(message = "Pickup location is required")
    @Valid
    private Location pickupLocation;
    
    @NotNull(message = "Delivery location is required")
    @Valid
    private Location deliveryLocation;
    
    // Scheduling
    @NotNull(message = "Pickup date is required")
    private Date pickupDateTime;
    
    @NotNull(message = "Requested delivery date is required")
    private Date requestedDeliveryDateTime;
    
    private Date actualDeliveryDateTime;
    
    // Cargo Information
    @NotBlank(message = "Cargo description is required")
    private String cargoDescription;
    
    @Positive(message = "Weight must be positive")
    private Double weightKg;
    
    @Positive(message = "Volume must be positive")
    private Double volumeCubicMeters;
    
    private String specialHandlingRequirements;
    
    private String additionalNotes;
    
    // Status
    @NotNull(message = "Status is required")
    private ShipmentStatus status;
    
    // Relationships
    @NotNull(message = "Customer is required")
    private Customer customer;
    
    private Vehicle assignedVehicle;
    
    private Driver assignedDriver;
    
    // Delivery Confirmation
    private DeliveryConfirmation deliveryConfirmation;

    // Default constructor
    public Shipment() {
        this.status = ShipmentStatus.PENDING;
    }

    // Constructor with essential fields
    public Shipment(String referenceNumber, Location pickupLocation, Location deliveryLocation,
                    Date pickupDateTime, Date requestedDeliveryDateTime, Customer customer,
                    String cargoDescription, Double weightKg, Double volumeCubicMeters) {
        this.referenceNumber = referenceNumber;
        this.pickupLocation = pickupLocation;
        this.deliveryLocation = deliveryLocation;
        this.pickupDateTime = pickupDateTime;
        this.requestedDeliveryDateTime = requestedDeliveryDateTime;
        this.customer = customer;
        this.cargoDescription = cargoDescription;
        this.weightKg = weightKg;
        this.volumeCubicMeters = volumeCubicMeters;
        this.status = ShipmentStatus.PENDING;
    }

    // Business Methods
    
    /**
     * Assign vehicle and driver to this shipment
     */
    public void assignResources(Vehicle vehicle, Driver driver) {
        if (this.status != ShipmentStatus.PENDING && this.status != ShipmentStatus.SCHEDULED) {
            throw new IllegalStateException("Can only assign resources to Pending or Scheduled shipments");
        }
        this.assignedVehicle = vehicle;
        this.assignedDriver = driver;
        this.status = ShipmentStatus.SCHEDULED;
    }
    
    /**
     * Release assigned resources
     */
    public void releaseResources() {
        this.assignedVehicle = null;
        this.assignedDriver = null;
    }
    
    /**
     * Mark shipment as picked up
     */
    public void markAsPickedUp() {
        if (this.status != ShipmentStatus.SCHEDULED) {
            throw new IllegalStateException("Can only pick up Scheduled shipments");
        }
        this.status = ShipmentStatus.PICKED_UP;
    }
    
    /**
     * Mark shipment as in transit
     */
    public void markAsInTransit() {
        if (this.status != ShipmentStatus.PICKED_UP) {
            throw new IllegalStateException("Can only mark as In Transit after Picked Up");
        }
        this.status = ShipmentStatus.IN_TRANSIT;
    }
    
    /**
     * Complete delivery with confirmation
     */
    public void completeDelivery(DeliveryConfirmation confirmation) {
        if (this.status != ShipmentStatus.IN_TRANSIT) {
            throw new IllegalStateException("Can only complete delivery for In Transit shipments");
        }
        this.deliveryConfirmation = confirmation;
        this.actualDeliveryDateTime = confirmation.getDeliveryDateTime();
        this.status = ShipmentStatus.DELIVERED;
        releaseResources();
    }
    
    /**
     * Cancel the shipment
     */
    public void cancel() {
        if (this.status == ShipmentStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel delivered shipments");
        }
        this.status = ShipmentStatus.CANCELED;
        releaseResources();
    }
    
    /**
     * Check if resources are assigned
     */
    public boolean hasResourcesAssigned() {
        return assignedVehicle != null && assignedDriver != null;
    }

    // Getters and Setters
    public Integer getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Integer shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Location getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(Location deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public Date getPickupDateTime() {
        return pickupDateTime;
    }

    public void setPickupDateTime(Date pickupDateTime) {
        this.pickupDateTime = pickupDateTime;
    }

    public Date getRequestedDeliveryDateTime() {
        return requestedDeliveryDateTime;
    }

    public void setRequestedDeliveryDateTime(Date requestedDeliveryDateTime) {
        this.requestedDeliveryDateTime = requestedDeliveryDateTime;
    }

    public Date getActualDeliveryDateTime() {
        return actualDeliveryDateTime;
    }

    public void setActualDeliveryDateTime(Date actualDeliveryDateTime) {
        this.actualDeliveryDateTime = actualDeliveryDateTime;
    }

    public String getCargoDescription() {
        return cargoDescription;
    }

    public void setCargoDescription(String cargoDescription) {
        this.cargoDescription = cargoDescription;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public Double getVolumeCubicMeters() {
        return volumeCubicMeters;
    }

    public void setVolumeCubicMeters(Double volumeCubicMeters) {
        this.volumeCubicMeters = volumeCubicMeters;
    }

    public String getSpecialHandlingRequirements() {
        return specialHandlingRequirements;
    }

    public void setSpecialHandlingRequirements(String specialHandlingRequirements) {
        this.specialHandlingRequirements = specialHandlingRequirements;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getAssignedVehicle() {
        return assignedVehicle;
    }

    public void setAssignedVehicle(Vehicle assignedVehicle) {
        this.assignedVehicle = assignedVehicle;
    }

    public Driver getAssignedDriver() {
        return assignedDriver;
    }

    public void setAssignedDriver(Driver assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public DeliveryConfirmation getDeliveryConfirmation() {
        return deliveryConfirmation;
    }

    public void setDeliveryConfirmation(DeliveryConfirmation deliveryConfirmation) {
        this.deliveryConfirmation = deliveryConfirmation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipment shipment = (Shipment) o;
        return Objects.equals(shipmentId, shipment.shipmentId) &&
                Objects.equals(referenceNumber, shipment.referenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipmentId, referenceNumber);
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "id=" + shipmentId +
                ", ref=" + referenceNumber +
                ", status=" + status +
                ", customer=" + (customer != null ? customer.getCompanyName() : "null") +
                '}';
    }
}
