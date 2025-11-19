package com.FTMS.FTMS_app.customer.application.service;

import com.FTMS.FTMS_app.customer.application.dto.CreateContractRequest;
import com.FTMS.FTMS_app.customer.application.dto.CreateCustomerRequest;
import com.FTMS.FTMS_app.customer.application.dto.ProcessPaymentRequest;
import com.FTMS.FTMS_app.customer.domain.model.Contract;
import com.FTMS.FTMS_app.customer.domain.model.Customer;
import com.FTMS.FTMS_app.customer.domain.model.Invoice;

import java.util.List;

public interface CustomerService {

    // --- Management Clienți ---
    Customer createCustomer(CreateCustomerRequest request);


    Customer updateCustomer(Long id, CreateCustomerRequest request);

    Customer getCustomerById(Long id);

    // --- Management Contracte ---
    Contract addContractToCustomer(CreateContractRequest request);

    // --- Management Financiar ---
    Invoice generateInvoice(Long customerId, Long shipmentId, double amount);

    Invoice processPayment(Long invoiceId, ProcessPaymentRequest request);

    /**
     * Returnează istoricul facturilor pentru un client.
     */
    List<Invoice> getInvoicesForCustomer(Long customerId);

    // --- Reguli de Business / Audit ---
    void checkCustomerOverdueStatus(Long customerId);
}