package com.FTMS.FTMS_app.fleet.application.dto;

import com.FTMS.FTMS_app.fleet.domain.model.VehicleType;
import com.fasterxml.jackson.annotation.JsonProperty; // <-- IMPORT NECESAR
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateVehicleRequest {

    @NotEmpty(message = "Registration number is required")
    @JsonProperty("registration_number")
    private String registrationNumber;

    @NotEmpty(message = "Make is required")
    // Nu e obligatoriu @JsonProperty aici (e un singur cuvânt), dar e ok să rămână implicit
    private String make;

    @NotEmpty(message = "Model is required")
    private String model;

    @NotNull(message = "Vehicle type is required")
    @JsonProperty("vehicle_type")
    private VehicleType vehicleType;

    @Min(value = 1990, message = "Year must be after 1990")
    @JsonProperty("year_of_manufacture")
    private int yearOfManufacture;

    @Min(value = 1, message = "Weight capacity must be positive")
    @JsonProperty("max_weight_kg")
    private double maxWeightKg;

    @Min(value = 1, message = "Volume capacity must be positive")
    @JsonProperty("max_volume_m3") // Folosim m3 pentru că e mai scurt în JSON
    private double maxVolumeCubicMeters;

    @NotEmpty(message = "Fuel type is required")
    @JsonProperty("fuel_type")
    private String fuelType;

    @Min(0)
    @JsonProperty("current_mileage")
    private double currentMileage;

    @NotEmpty(message = "Insurance policy number is required")
    @JsonProperty("insurance_policy_number")
    private String insurancePolicyNumber;

    @NotNull
    @Future(message = "Insurance expiry date must be in the future")
    @JsonProperty("insurance_expiry_date")
    private LocalDate insuranceExpiryDate;

    @NotNull
    @Future(message = "Registration expiry date must be in the future")
    @JsonProperty("registration_expiry_date")
    private LocalDate registrationExpiryDate;
}