package com.FTMS.FTMS_app.customer.presentation;

import com.FTMS.FTMS_app.customer.application.dto.CreateContractRequest;
import com.FTMS.FTMS_app.customer.application.dto.CreateCustomerRequest;
import com.FTMS.FTMS_app.customer.application.dto.ProcessPaymentRequest;
import com.FTMS.FTMS_app.customer.application.service.CustomerService;
import com.FTMS.FTMS_app.customer.domain.model.Contract;
import com.FTMS.FTMS_app.customer.domain.model.Customer;
import com.FTMS.FTMS_app.customer.domain.model.Invoice;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        Customer newCustomer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping("/contracts")
    public ResponseEntity<Contract> addContract(@Valid @RequestBody CreateContractRequest request) {
        Contract newContract = customerService.addContractToCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newContract);
    }

    @PostMapping("/invoices/{invoiceId}/pay")
    public ResponseEntity<Invoice> processPayment(
            @PathVariable Long invoiceId,
            @Valid @RequestBody ProcessPaymentRequest request) {
        Invoice updatedInvoice = customerService.processPayment(invoiceId, request);
        return ResponseEntity.ok(updatedInvoice);
    }
}