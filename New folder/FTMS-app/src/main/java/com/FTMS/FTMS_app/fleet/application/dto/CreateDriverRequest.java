package com.FTMS.FTMS_app.fleet.application.dto;

import com.FTMS.FTMS_app.fleet.domain.model.LicenseType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDriverRequest {

    @NotEmpty
    private String name;

    // License Info
    @NotEmpty
    private String licenseNumber;
    @NotNull
    private LicenseType licenseType;
    @NotNull
    private LocalDate licenseIssueDate;
    @NotNull
    @Future(message = "License expiry date must be in the future")
    private LocalDate licenseExpiryDate;

    // Contact Details
    @NotEmpty
    private String phone;
    @Email
    private String email;
    private String address;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;

    @NotNull
    private LocalDate employmentDate;
}