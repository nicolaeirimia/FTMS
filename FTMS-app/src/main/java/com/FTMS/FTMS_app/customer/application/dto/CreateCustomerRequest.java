package com.FTMS.FTMS_app.customer.application.dto;

import com.FTMS.FTMS_app.customer.domain.model.CustomerCategory;
import com.FTMS.FTMS_app.customer.domain.model.PaymentTerms;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateCustomerRequest {

    @NotEmpty
    private String companyName;

    @NotEmpty(message = "Tax ID Number (CUI) is required.")
    private String taxIdNumber;

    private String registrationNumber;

    @NotEmpty
    private String primaryContactName;

    @NotEmpty
    private String primaryContactPhone;

    @NotEmpty
    @Email(message = "A valid email is required.")
    private String primaryContactEmail;

    @NotNull
    @Valid // Validează și câmpurile din AddressDto
    private AddressDto billingAddress;

    @Valid
    private List<AddressDto> deliveryAddresses;

    @NotNull
    private PaymentTerms paymentTerms;

    @NotNull
    private CustomerCategory category;

    @Min(0)
    private double creditLimit;
}