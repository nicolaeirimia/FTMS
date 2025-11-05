package com.FTMS.FTMS_app.fleet.application.dto;

import com.FTMS.FTMS_app.fleet.domain.model.MaintenanceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MaintenanceRecordDto {

    @NotNull
    private LocalDate date;

    @NotNull
    private MaintenanceType maintenanceType;

    private String description;

    @Min(0)
    private double cost;

    private String serviceProvider;
}