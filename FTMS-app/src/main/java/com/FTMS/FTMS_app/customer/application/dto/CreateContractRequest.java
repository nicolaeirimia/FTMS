package com.FTMS.FTMS_app.customer.application.dto;

import com.FTMS.FTMS_app.customer.domain.model.ServiceLevel; // <-- Import Enum
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateContractRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotNull(message = "Service level is required")
    private ServiceLevel serviceLevel; // <-- Folosim Enum direct!

    @Min(value = 0, message = "Discount rate cannot be negative")
    @Max(value = 1, message = "Discount rate cannot correspond to more than 100%")
    private double discountRate;
}