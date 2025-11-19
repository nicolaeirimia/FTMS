package com.FTMS.FTMS_app.customer.application.service;

import com.FTMS.FTMS_app.customer.application.dto.CreateContractRequest;
import com.FTMS.FTMS_app.customer.application.dto.CreateCustomerRequest;
import com.FTMS.FTMS_app.customer.application.dto.ProcessPaymentRequest;
import com.FTMS.FTMS_app.customer.domain.model.Contract;
import com.FTMS.FTMS_app.customer.domain.model.Customer;
import com.FTMS.FTMS_app.customer.domain.model.Invoice;

import java.util.List;

public interface CustomerService {


    Customer createCustomer(CreateCustomerRequest request);


    Customer updateCustomer(Long id, CreateCustomerRequest request);

    Customer getCustomerById(Long id);


    Contract addContractToCustomer(CreateContractRequest request);


    Invoice generateInvoice(Long customerId, Long shipmentId, double amount);

    Invoice processPayment(Long invoiceId, ProcessPaymentRequest request);


    List<Invoice> getInvoicesForCustomer(Long customerId);


    void checkCustomerOverdueStatus(Long customerId);
}