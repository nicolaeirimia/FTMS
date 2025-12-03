package com.FTMS.FTMS_app.customer.application.dto;

import com.FTMS.FTMS_app.customer.domain.model.ServiceLevel;
import com.fasterxml.jackson.annotation.JsonProperty; // <-- IMPORT NECESAR
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateContractRequest {

    @NotNull(message = "Customer ID is required")
    @JsonProperty("customer_id") // În JSON va fi "customer_id"
    private Long customerId;

    @NotNull(message = "Start date is required")
    @JsonProperty("start_date")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    @JsonProperty("end_date")
    private LocalDate endDate;

    @NotNull(message = "Service level is required")
    @JsonProperty("service_level")
    private ServiceLevel serviceLevel;

    @Min(value = 0, message = "Discount rate cannot be negative")
    @Max(value = 1, message = "Discount rate cannot correspond to more than 100%")
    @JsonProperty("discount_rate")
    private double discountRate;
}