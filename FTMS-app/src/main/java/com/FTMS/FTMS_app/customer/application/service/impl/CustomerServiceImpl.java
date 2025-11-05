package com.FTMS.FTMS_app.customer.application.service.impl;

import com.FTMS.FTMS_app.customer.application.dto.AddressDto;
import com.FTMS.FTMS_app.customer.application.dto.CreateContractRequest;
import com.FTMS.FTMS_app.customer.application.dto.CreateCustomerRequest;
import com.FTMS.FTMS_app.customer.application.dto.ProcessPaymentRequest;
import com.FTMS.FTMS_app.customer.application.service.CustomerService;
import com.FTMS.FTMS_app.customer.domain.model.*;
import com.FTMS.FTMS_app.customer.domain.repository.ContractRepository; // Va trebui să creăm acest Repository
import com.FTMS.FTMS_app.customer.domain.repository.CustomerRepository;
import com.FTMS.FTMS_app.customer.domain.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository; // Adăugat

    // Constructor Injection
    public CustomerServiceImpl(CustomerRepository customerRepository,
                               InvoiceRepository invoiceRepository,
                               ContractRepository contractRepository) {
        this.customerRepository = customerRepository;
        this.invoiceRepository = invoiceRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    public Customer createCustomer(CreateCustomerRequest request) {
        // 1. Validare (Domain Validation)
        customerRepository.findByTaxIdNumber(request.getTaxIdNumber())
                .ifPresent(c -> { throw new IllegalArgumentException("Customer with Tax ID " + request.getTaxIdNumber() + " already exists."); });
        customerRepository.findByPrimaryContactEmail(request.getPrimaryContactEmail())
                .ifPresent(c -> { throw new IllegalArgumentException("Customer with email " + request.getPrimaryContactEmail() + " already exists."); });

        // 2. Mapare (DTO -> Domain Model)
        Address billingAddress = mapToAddress(request.getBillingAddress());
        List<Address> deliveryAddresses = request.getDeliveryAddresses().stream()
                .map(this::mapToAddress)
                .collect(Collectors.toList());

        Customer customer = new Customer(
                null, // ID
                request.getCompanyName(),
                request.getTaxIdNumber(),
                request.getRegistrationNumber(),
                request.getPrimaryContactName(),
                request.getPrimaryContactPhone(),
                request.getPrimaryContactEmail(),
                billingAddress,
                deliveryAddresses,
                request.getPaymentTerms(),
                request.getCategory(),
                request.getCreditLimit(),
                CustomerStatus.ACTIVE, // Status inițial
                null // Fără contract la creare
        );

        // 3. Salvare
        return customerRepository.save(customer);
    }

    @Override
    public Contract addContractToCustomer(CreateContractRequest request) {
        // 1. Găsește entitatea rădăcină (Customer)
        Customer customer = getCustomerById(request.getCustomerId());

        // 2. Mapare (DTO -> Domain Model)
        Contract contract = new Contract(
                null, // ID
                customer,
                request.getStartDate(),
                request.getEndDate(),
                request.getServiceLevel(),
                request.getDiscountRate()
        );

        // 3. Salvare (putem salva direct contractul)
        Contract savedContract = contractRepository.save(contract);

        // 4. Actualizează legătura în Agregat și salvează
        customer.setContract(savedContract);
        customerRepository.save(customer);

        return savedContract;
    }

    @Override
    @Transactional
    public Invoice processPayment(Long invoiceId, ProcessPaymentRequest request) {
        // 1. Găsește entitatea (Invoice)
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId)); // Vom folosi o excepție custom

        // 2. Mapare (DTO -> Value Object)
        PaymentDetails paymentDetails = new PaymentDetails(
                request.getPaymentDate(),
                request.getAmount(),
                request.getPaymentMethod(),
                request.getReferenceNumber()
        );

        // 3. Apelează logica de business din domeniu
        invoice.recordPayment(paymentDetails);

        // 4. Salvează
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        // 5. Verifică statusul clientului după plată
        checkCustomerOverdueStatus(updatedInvoice.getCustomer().getId());

        return updatedInvoice;
    }

    @Override
    @Transactional
    public void checkCustomerOverdueStatus(Long customerId) {
        // Aceasta este regula de business din cerințe:
        // "Customers with overdue invoices exceeding their credit limit are automatically flagged,
        // and new shipment requests.http are blocked until payments are received."

        Customer customer = getCustomerById(customerId);

        // 1. Calculează totalul restant (folosind query-ul din repository)
        Double overdueAmount = invoiceRepository.getOverdueAmountByCustomerId(customerId);
        if (overdueAmount == null) {
            overdueAmount = 0.0;
        }

        // 2. Aplică logica
        if (overdueAmount > customer.getCreditLimit()) {
            if (customer.getStatus() == CustomerStatus.ACTIVE) {
                customer.suspendAccount(); // Metodă din domeniu
                customerRepository.save(customer);
            }
        } else {
            // Dacă au plătit și au intrat sub limită, reactivăm contul
            if (customer.getStatus() == CustomerStatus.SUSPENDED) {
                customer.activateAccount(); // Metodă din domeniu
                customerRepository.save(customer);
            }
        }
    }

    @Override
    @Transactional
    public Invoice generateInvoice(Long customerId, Long shipmentId, double amount) {
        Customer customer = getCustomerById(customerId);

        // Calculează discount (dacă există contract activ)
        double discount = 0.0;
        if (customer.getContract() != null && customer.getContract().isActive()) {
            discount = amount * customer.getContract().getDiscountRate();
        }

        double finalAmount = amount - discount;
        double taxes = finalAmount * 0.19; // Exemplu: TVA 19%
        double totalAmount = finalAmount + taxes;

        Invoice invoice = new Invoice(
                null, // ID
                "INV-" + shipmentId, // Generare număr factură (simplificat)
                customer,
                shipmentId,
                LocalDate.now(),
                LocalDate.now().plusDays(getPaymentDays(customer.getPaymentTerms())), // Due Date
                finalAmount,
                taxes,
                totalAmount,
                InvoiceStatus.PENDING,
                null // Fără detalii de plată
        );

        return invoiceRepository.save(invoice);
    }


    // --- Metode Utilitare (Helpers) ---

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id)); // Vom folosi o excepție custom
    }

    // Mapare din DTO în Value Object
    private Address mapToAddress(AddressDto dto) {
        return new Address(
                dto.getStreet(),
                dto.getCity(),
                dto.getState(),
                dto.getZipCode(),
                dto.getCountry()
        );
    }

    // Calculează zilele de plată
    private long getPaymentDays(PaymentTerms terms) {
        switch (terms) {
            case NET_15: return 15;
            case NET_30: return 30;
            case NET_60: return 60;
            case PREPAID:
            default: return 0;
        }
    }
}