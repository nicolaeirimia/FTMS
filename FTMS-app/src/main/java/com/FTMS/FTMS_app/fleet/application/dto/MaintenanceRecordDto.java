package com.FTMS.FTMS_app.fleet.application.dto;

import com.FTMS.FTMS_app.fleet.domain.model.MaintenanceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MaintenanceRecordDto {

    @NotNull
    @PastOrPresent(message = "Maintenance date cannot be in the future")
    private LocalDate date;
    @NotNull
    private MaintenanceType maintenanceType;

    @NotEmpty(message = "Description is required")
    private String description;

    @Min(value = 0, message = "Cost cannot be negative")
    private double cost;

    @NotEmpty(message = "Service provider is required")
    private String serviceProvider;
}