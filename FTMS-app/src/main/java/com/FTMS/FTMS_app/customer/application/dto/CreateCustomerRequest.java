package com.FTMS.FTMS_app.customer.application.dto;

import com.FTMS.FTMS_app.customer.domain.model.CustomerCategory;
import com.FTMS.FTMS_app.customer.domain.model.PaymentTerms;
import com.fasterxml.jackson.annotation.JsonProperty; // <-- IMPORT NECESAR
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CreateCustomerRequest {

    @NotBlank(message = "Company name is required")
    @JsonProperty("company_name")
    private String companyName;

    @NotBlank(message = "Tax ID Number (CUI) is required")
    @JsonProperty("tax_id_number")
    private String taxIdNumber;

    @JsonProperty("registration_number")
    private String registrationNumber; // Opțional

    @NotBlank(message = "Primary contact name is required")
    @JsonProperty("primary_contact_name")
    private String primaryContactName;

    @NotBlank(message = "Primary contact phone is required")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
    @JsonProperty("primary_contact_phone")
    private String primaryContactPhone;

    @NotBlank(message = "Primary contact email is required")
    @Email(message = "Invalid email format")
    @JsonProperty("primary_contact_email")
    private String primaryContactEmail;

    @NotNull(message = "Billing address is required")
    @Valid
    @JsonProperty("billing_address")
    private AddressDto billingAddress;

    @Valid
    @JsonProperty("delivery_addresses")
    private List<AddressDto> deliveryAddresses;

    @NotNull(message = "Payment terms are required")
    @JsonProperty("payment_terms")
    private PaymentTerms paymentTerms;

    @NotNull(message = "Customer category is required")
    @JsonProperty("category")
    private CustomerCategory category;

    @Min(value = 0, message = "Credit limit cannot be negative")
    @JsonProperty("credit_limit")
    private double creditLimit;
}