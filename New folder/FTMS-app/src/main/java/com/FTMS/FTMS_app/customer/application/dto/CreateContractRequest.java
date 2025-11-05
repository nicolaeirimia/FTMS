package com.FTMS.FTMS_app.customer.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateContractRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    @Future(message = "End date must be in the future.")
    private LocalDate endDate;

    @NotNull
    private String serviceLevel;

    private double discountRate;
}