package com.ftms.domain.fleet;

import com.ftms.domain.common.DriverStatus;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.Objects;

/**
 * Driver Entity - Aggregate Root
 * Manages driver information and assignments
 */
public class Driver {
    
    private Integer driverId;
    
    @NotBlank(message = "Driver name is required")
    private String name;
    
    @NotBlank(message = "Phone number is required")
    private String phone;
    
    @Email(message = "Valid email is required")
    private String email;
    
    @NotBlank(message = "License number is required")
    private String licenseNumber;
    
    @NotBlank(message = "License type is required")
    private String licenseType; // C, CE, etc.
    
    @NotNull(message = "License issue date is required")
    private Date licenseIssueDate;
    
    @NotNull(message = "License expiry date is required")
    private Date licenseExpiryDate;
    
    private Date dateOfEmployment;
    
    private String emergencyContact;
    
    @NotNull(message = "Status is required")
    private DriverStatus status;
    
    private Vehicle primaryVehicle;

    public Driver() {
        this.status = DriverStatus.AVAILABLE;
    }

    public Driver(String name, String phone, String email, String licenseNumber, 
                  String licenseType, Date licenseIssueDate, Date licenseExpiryDate) {
        this();
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.licenseNumber = licenseNumber;
        this.licenseType = licenseType;
        this.licenseIssueDate = licenseIssueDate;
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public boolean isAvailable() {
        return this.status == DriverStatus.AVAILABLE;
    }
    
    public boolean hasValidLicense() {
        return this.licenseExpiryDate != null && 
               this.licenseExpiryDate.after(new Date());
    }
    
    public boolean canDriveVehicle(Vehicle vehicle) {
        // Simplified - in reality would check license type compatibility
        return hasValidLicense();
    }
    
    public void markAsOnRoute() {
        if (this.status != DriverStatus.AVAILABLE) {
            throw new IllegalStateException("Driver must be available");
        }
        this.status = DriverStatus.ON_ROUTE;
    }
    
    public void makeAvailable() {
        if (this.status == DriverStatus.OFF_DUTY) {
            throw new IllegalStateException("Off-duty drivers cannot be made available");
        }
        this.status = DriverStatus.AVAILABLE;
    }

    // Getters and Setters
    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public Date getLicenseIssueDate() {
        return licenseIssueDate;
    }

    public void setLicenseIssueDate(Date licenseIssueDate) {
        this.licenseIssueDate = licenseIssueDate;
    }

    public Date getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public void setLicenseExpiryDate(Date licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public Date getDateOfEmployment() {
        return dateOfEmployment;
    }

    public void setDateOfEmployment(Date dateOfEmployment) {
        this.dateOfEmployment = dateOfEmployment;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public Vehicle getPrimaryVehicle() {
        return primaryVehicle;
    }

    public void setPrimaryVehicle(Vehicle primaryVehicle) {
        this.primaryVehicle = primaryVehicle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return Objects.equals(driverId, driver.driverId) &&
                Objects.equals(licenseNumber, driver.licenseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, licenseNumber);
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + driverId +
                ", name=" + name +
                ", license=" + licenseNumber +
                ", status=" + status +
                '}';
    }
}
