package com.FTMS.FTMS_app.fleet.application.dto;

import com.FTMS.FTMS_app.fleet.domain.model.VehicleType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data // Generează Getters, Setters, toString, etc.
public class CreateVehicleRequest {

    @NotEmpty(message = "Registration number is required")
    private String registrationNumber;

    @NotEmpty
    private String make;

    @NotEmpty
    private String model;

    @NotNull
    private VehicleType vehicleType;

    @Min(1990)
    private int yearOfManufacture;

    @Min(1)
    private double maxWeightKg;

    @Min(1)
    private double maxVolumeCubicMeters;

    private String fuelType;

    @Min(0)
    private double currentMileage;

    @NotEmpty
    private String insurancePolicyNumber;

    @NotNull
    @Future(message = "Insurance expiry date must be in the future")
    private LocalDate insuranceExpiryDate;

    @NotNull
    @Future(message = "Registration expiry date must be in the future")
    private LocalDate registrationExpiryDate;
}