package com.FTMS.FTMS_app.fleet.application.dto;

import com.FTMS.FTMS_app.fleet.domain.model.LicenseType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDriverRequest {

    @NotEmpty(message = "Name is required")
    private String name;

    // --- License Info ---
    @NotEmpty(message = "License number is required")
    private String licenseNumber;

    @NotNull(message = "License type is required")
    private LicenseType licenseType;

    @NotNull
    @PastOrPresent(message = "Issue date cannot be in the future") // <-- ADAUGAT
    private LocalDate licenseIssueDate;

    @NotNull
    @Future(message = "License expiry date must be in the future")
    private LocalDate licenseExpiryDate;

    // --- Contact Details ---
    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone format") // <-- ADAUGAT (opțional)
    private String phone;

    @NotEmpty(message = "Email is required") // <-- ADAUGAT (dacă e obligatoriu)
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Address is required") // <-- ADAUGAT (dacă e obligatoriu)
    private String address;

    // --- Emergency Contact (Presupunem că sunt opționale, deci le lăsăm așa) ---
    private String emergencyContactName;
    private String emergencyContactPhone;

    @NotNull
    private LocalDate employmentDate;
}