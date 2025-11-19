package com.FTMS.FTMS_app.customer.application.dto;

import com.FTMS.FTMS_app.customer.domain.model.CustomerCategory;
import com.FTMS.FTMS_app.customer.domain.model.PaymentTerms;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CreateCustomerRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Tax ID Number (CUI) is required")
    private String taxIdNumber;

    private String registrationNumber; // Opțional

    @NotBlank(message = "Primary contact name is required")
    private String primaryContactName;

    @NotBlank(message = "Primary contact phone is required")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
    private String primaryContactPhone;

    @NotBlank(message = "Primary contact email is required")
    @Email(message = "Invalid email format")
    private String primaryContactEmail;

    @NotNull(message = "Billing address is required")
    @Valid
    private AddressDto billingAddress;

    @Valid
    private List<AddressDto> deliveryAddresses;

    @NotNull(message = "Payment terms are required")
    private PaymentTerms paymentTerms;

    @NotNull(message = "Customer category is required")
    private CustomerCategory category;

    @Min(value = 0, message = "Credit limit cannot be negative")
    private double creditLimit;
}