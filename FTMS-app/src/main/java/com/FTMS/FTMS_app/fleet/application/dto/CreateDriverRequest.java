package com.FTMS.FTMS_app.fleet.application.dto;

import com.FTMS.FTMS_app.fleet.domain.model.LicenseType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDriverRequest {

    @NotEmpty(message = "Name is required")
    @JsonProperty("full_name") // <-- MODIFICAT: Consistent cu restul
    private String name;

    @NotEmpty(message = "License number is required")
    @JsonProperty("license_number")
    private String licenseNumber;

    @NotNull(message = "License type is required")
    @JsonProperty("license_type")
    private LicenseType licenseType;

    @NotNull
    @PastOrPresent(message = "Issue date cannot be in the future")
    @JsonProperty("license_issue_date")
    private LocalDate licenseIssueDate;

    @NotNull
    @Future(message = "License expiry date must be in the future")
    @JsonProperty("license_expiry_date")
    private LocalDate licenseExpiryDate;

    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone format")
    @JsonProperty("phone_number") // <-- MODIFICAT: Consistent
    private String phone;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")

    private String email;

    @NotEmpty(message = "Address is required")
    private String address;

    @JsonProperty("emergency_contact_name")
    private String emergencyContactName;

    @JsonProperty("emergency_contact_phone")
    private String emergencyContactPhone;

    @NotNull
    @JsonProperty("employment_date")
    private LocalDate employmentDate;
}