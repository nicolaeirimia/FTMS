package com.FTMS.FTMS_app.fleet.application.dto;

import com.FTMS.FTMS_app.fleet.domain.model.LicenseType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDriverRequest {

    @NotEmpty(message = "Name is required")
    private String name;


    @NotEmpty(message = "License number is required")
    private String licenseNumber;

    @NotNull(message = "License type is required")
    private LicenseType licenseType;

    @NotNull
    @PastOrPresent(message = "Issue date cannot be in the future")
    private LocalDate licenseIssueDate;

    @NotNull
    @Future(message = "License expiry date must be in the future")
    private LocalDate licenseExpiryDate;


    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone format")
    private String phone;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Address is required")
    private String address;


    private String emergencyContactName;
    private String emergencyContactPhone;

    @NotNull
    private LocalDate employmentDate;
}