package com.ftms.domain.customer;

import com.ftms.domain.common.Address;
import com.ftms.domain.common.CustomerStatus;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Customer Entity - Aggregate Root
 */
public class Customer {
    
    private Integer customerId;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @NotBlank(message = "Tax ID is required")
    private String taxIdentificationNumber;
    
    private String businessRegistrationNumber;
    
    @NotBlank(message = "Primary contact name is required")
    private String primaryContactName;
    
    @NotBlank(message = "Contact phone is required")
    private String contactPhone;
    
    @Email(message = "Valid email is required")
    private String contactEmail;
    
    @NotNull(message = "Billing address is required")
    @Valid
    private Address billingAddress;
    
    private List<Address> deliveryAddresses;
    
    private String paymentTerms; // prepaid, net 15, net 30, net 60
    
    private String customerCategory; // Standard, Premium, VIP
    
    @PositiveOrZero(message = "Credit limit must be non-negative")
    private Double creditLimit;
    
    @NotNull(message = "Status is required")
    private CustomerStatus status;
    
    private List<Contract> contracts;

    public Customer() {
        this.status = CustomerStatus.ACTIVE;
        this.deliveryAddresses = new ArrayList<>();
        this.contracts = new ArrayList<>();
    }

    public Customer(String companyName, String taxIdentificationNumber,
                    String primaryContactName, String contactPhone, String contactEmail,
                    Address billingAddress) {
        this();
        this.companyName = companyName;
        this.taxIdentificationNumber = taxIdentificationNumber;
        this.primaryContactName = primaryContactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.billingAddress = billingAddress;
    }

    public boolean isActive() {
        return this.status == CustomerStatus.ACTIVE;
    }
    
    public void suspend() {
        this.status = CustomerStatus.SUSPENDED;
    }
    
    public void activate() {
        this.status = CustomerStatus.ACTIVE;
    }
    
    public void addDeliveryAddress(Address address) {
        this.deliveryAddresses.add(address);
    }
    
    public void addContract(Contract contract) {
        this.contracts.add(contract);
    }

    // Getters and Setters
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxIdentificationNumber() {
        return taxIdentificationNumber;
    }

    public void setTaxIdentificationNumber(String taxIdentificationNumber) {
        this.taxIdentificationNumber = taxIdentificationNumber;
    }

    public String getBusinessRegistrationNumber() {
        return businessRegistrationNumber;
    }

    public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public String getPrimaryContactName() {
        return primaryContactName;
    }

    public void setPrimaryContactName(String primaryContactName) {
        this.primaryContactName = primaryContactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public List<Address> getDeliveryAddresses() {
        return deliveryAddresses;
    }

    public void setDeliveryAddresses(List<Address> deliveryAddresses) {
        this.deliveryAddresses = deliveryAddresses;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getCustomerCategory() {
        return customerCategory;
    }

    public void setCustomerCategory(String customerCategory) {
        this.customerCategory = customerCategory;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId) &&
                Objects.equals(taxIdentificationNumber, customer.taxIdentificationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, taxIdentificationNumber);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + customerId +
                ", company=" + companyName +
                ", status=" + status +
                '}';
    }
}
