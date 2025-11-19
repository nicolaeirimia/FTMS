package com.FTMS.FTMS_app.fleet.application.dto;

import com.FTMS.FTMS_app.fleet.domain.model.VehicleType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateVehicleRequest {

    @NotEmpty(message = "Registration number is required")
    private String registrationNumber;

    @NotEmpty(message = "Make is required")
    private String make;

    @NotEmpty(message = "Model is required")
    private String model;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @Min(value = 1990, message = "Year must be after 1990")
    private int yearOfManufacture;

    @Min(value = 1, message = "Weight capacity must be positive")
    private double maxWeightKg;

    @Min(value = 1, message = "Volume capacity must be positive")
    private double maxVolumeCubicMeters;

    @NotEmpty(message = "Fuel type is required") // <-- ADAUGAT
    private String fuelType;

    @Min(0)
    private double currentMileage;

    @NotEmpty(message = "Insurance policy number is required")
    private String insurancePolicyNumber;

    @NotNull
    @Future(message = "Insurance expiry date must be in the future")
    private LocalDate insuranceExpiryDate;

    @NotNull
    @Future(message = "Registration expiry date must be in the future")
    private LocalDate registrationExpiryDate;
}