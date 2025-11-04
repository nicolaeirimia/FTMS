package com.ftms.domain.fleet;

import com.ftms.domain.common.VehicleStatus;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Vehicle Entity - Aggregate Root
 * Manages fleet vehicle information and maintenance
 */
public class Vehicle {
    
    // Identity
    private Integer vehicleId;
    
    @NotBlank(message = "Registration number is required")
    private String registrationNumber;
    
    @NotBlank(message = "Make and model is required")
    private String makeAndModel;
    
    @NotBlank(message = "Vehicle type is required")
    private String vehicleType; // box truck, flatbed, refrigerated, tanker
    
    @NotNull(message = "Year of manufacture is required")
    @Min(value = 1900, message = "Year must be after 1900")
    private Integer yearOfManufacture;
    
    // Capacity
    @Positive(message = "Maximum weight capacity must be positive")
    private Double maxWeightKg;
    
    @Positive(message = "Maximum volume capacity must be positive")
    private Double maxVolumeCubicMeters;
    
    // Operations
    private String fuelType;
    
    @PositiveOrZero(message = "Current mileage must be non-negative")
    private Integer currentMileage;
    
    // Insurance and Registration
    private String insurancePolicyNumber;
    
    private Date insuranceExpiryDate;
    
    private Date registrationExpiryDate;
    
    // Status
    @NotNull(message = "Status is required")
    private VehicleStatus status;
    
    // Maintenance Records (part of aggregate)
    private List<MaintenanceRecord> maintenanceRecords;

    // Default constructor
    public Vehicle() {
        this.status = VehicleStatus.AVAILABLE;
        this.maintenanceRecords = new ArrayList<>();
    }

    // Constructor with essential fields
    public Vehicle(String registrationNumber, String makeAndModel, String vehicleType,
                   Integer yearOfManufacture, Double maxWeightKg, Double maxVolumeCubicMeters) {
        this();
        this.registrationNumber = registrationNumber;
        this.makeAndModel = makeAndModel;
        this.vehicleType = vehicleType;
        this.yearOfManufacture = yearOfManufacture;
        this.maxWeightKg = maxWeightKg;
        this.maxVolumeCubicMeters = maxVolumeCubicMeters;
    }

    // Business Methods
    
    /**
     * Check if vehicle is available for assignment
     */
    public boolean isAvailable() {
        return this.status == VehicleStatus.AVAILABLE;
    }
    
    /**
     * Check if vehicle can handle cargo
     */
    public boolean canHandle(Double weightKg, Double volumeCubicMeters) {
        return weightKg <= this.maxWeightKg && volumeCubicMeters <= this.maxVolumeCubicMeters;
    }
    
    /**
     * Mark vehicle as in use
     */
    public void markAsInUse() {
        if (this.status != VehicleStatus.AVAILABLE) {
            throw new IllegalStateException("Vehicle must be available to be marked as in use");
        }
        this.status = VehicleStatus.IN_USE;
    }
    
    /**
     * Make vehicle available
     */
    public void makeAvailable() {
        if (this.status == VehicleStatus.OUT_OF_SERVICE) {
            throw new IllegalStateException("Out of service vehicles cannot be made available");
        }
        this.status = VehicleStatus.AVAILABLE;
    }
    
    /**
     * Schedule maintenance
     */
    public void scheduleMaintenance(MaintenanceRecord record) {
        this.maintenanceRecords.add(record);
        this.status = VehicleStatus.IN_MAINTENANCE;
    }
    
    /**
     * Complete maintenance
     */
    public void completeMaintenance() {
        if (this.status != VehicleStatus.IN_MAINTENANCE) {
            throw new IllegalStateException("Vehicle is not in maintenance");
        }
        this.status = VehicleStatus.AVAILABLE;
    }
    
    /**
     * Take vehicle out of service
     */
    public void takeOutOfService() {
        this.status = VehicleStatus.OUT_OF_SERVICE;
    }

    // Getters and Setters
    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getMakeAndModel() {
        return makeAndModel;
    }

    public void setMakeAndModel(String makeAndModel) {
        this.makeAndModel = makeAndModel;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Integer getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(Integer yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }

    public Double getMaxWeightKg() {
        return maxWeightKg;
    }

    public void setMaxWeightKg(Double maxWeightKg) {
        this.maxWeightKg = maxWeightKg;
    }

    public Double getMaxVolumeCubicMeters() {
        return maxVolumeCubicMeters;
    }

    public void setMaxVolumeCubicMeters(Double maxVolumeCubicMeters) {
        this.maxVolumeCubicMeters = maxVolumeCubicMeters;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Integer getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(Integer currentMileage) {
        this.currentMileage = currentMileage;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }

    public Date getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(Date insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public Date getRegistrationExpiryDate() {
        return registrationExpiryDate;
    }

    public void setRegistrationExpiryDate(Date registrationExpiryDate) {
        this.registrationExpiryDate = registrationExpiryDate;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public List<MaintenanceRecord> getMaintenanceRecords() {
        return maintenanceRecords;
    }

    public void setMaintenanceRecords(List<MaintenanceRecord> maintenanceRecords) {
        this.maintenanceRecords = maintenanceRecords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(vehicleId, vehicle.vehicleId) &&
                Objects.equals(registrationNumber, vehicle.registrationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleId, registrationNumber);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + vehicleId +
                ", reg=" + registrationNumber +
                ", type=" + vehicleType +
                ", status=" + status +
                '}';
    }
}
