package com.FTMS.FTMS_app.customer.application.service;

import com.FTMS.FTMS_app.customer.application.dto.CreateContractRequest;
import com.FTMS.FTMS_app.customer.application.dto.CreateCustomerRequest;
import com.FTMS.FTMS_app.customer.application.dto.ProcessPaymentRequest;
import com.FTMS.FTMS_app.customer.domain.model.Contract;
import com.FTMS.FTMS_app.customer.domain.model.Customer;
import com.FTMS.FTMS_app.customer.domain.model.Invoice;

public interface CustomerService {

    /**
     * Use Case: Înregistrarea unui client nou.
     */
    Customer createCustomer(CreateCustomerRequest request);

    /**
     * Use Case: Adăugarea unui contract pentru un client.
     */
    Contract addContractToCustomer(CreateContractRequest request);

    /**
     * Use Case: Procesarea unei plăți pentru o factură.
     */
    Invoice processPayment(Long invoiceId, ProcessPaymentRequest request);

    /**
     * Use Case: Verificarea limitelor de credit și suspendarea conturilor.
     * Aceasta implementează regula de business din cerințe.
     */
    void checkCustomerOverdueStatus(Long customerId);

    /**
     * Use Case: Generarea unei facturi (va fi apelat de modulul Shipment).
     * Definim doar "scheletul" acum.
     */
    Invoice generateInvoice(Long customerId, Long shipmentId, double amount);

    /**
     * Metodă utilitară pentru a obține un client.
     */
    Customer getCustomerById(Long id);
}