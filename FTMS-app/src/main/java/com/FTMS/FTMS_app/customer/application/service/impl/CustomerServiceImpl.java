package com.FTMS.FTMS_app.customer.application.service.impl;

import com.FTMS.FTMS_app.common.exception.ResourceNotFoundException;
import com.FTMS.FTMS_app.customer.application.dto.AddressDto;
import com.FTMS.FTMS_app.customer.application.dto.CreateContractRequest;
import com.FTMS.FTMS_app.customer.application.dto.CreateCustomerRequest;
import com.FTMS.FTMS_app.customer.application.dto.ProcessPaymentRequest;
import com.FTMS.FTMS_app.customer.application.service.CustomerService;
import com.FTMS.FTMS_app.customer.domain.model.*;
import com.FTMS.FTMS_app.customer.domain.repository.ContractRepository;
import com.FTMS.FTMS_app.customer.domain.repository.CustomerRepository;
import com.FTMS.FTMS_app.customer.domain.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               InvoiceRepository invoiceRepository,
                               ContractRepository contractRepository) {
        this.customerRepository = customerRepository;
        this.invoiceRepository = invoiceRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    public Customer createCustomer(CreateCustomerRequest request) {
        customerRepository.findByTaxIdNumber(request.getTaxIdNumber())
                .ifPresent(c -> { throw new IllegalArgumentException("Customer with Tax ID " + request.getTaxIdNumber() + " already exists."); });
        customerRepository.findByPrimaryContactEmail(request.getPrimaryContactEmail())
                .ifPresent(c -> { throw new IllegalArgumentException("Customer with email " + request.getPrimaryContactEmail() + " already exists."); });

        Address billingAddress = mapToAddress(request.getBillingAddress());

        List<Address> deliveryAddresses = new ArrayList<>();
        if (request.getDeliveryAddresses() != null) {
            deliveryAddresses = request.getDeliveryAddresses().stream()
                    .map(this::mapToAddress)
                    .collect(Collectors.toList());
        }

        Customer customer = Customer.builder()
                .companyName(request.getCompanyName())
                .taxIdNumber(request.getTaxIdNumber())
                .registrationNumber(request.getRegistrationNumber())
                .primaryContactName(request.getPrimaryContactName())
                .primaryContactPhone(request.getPrimaryContactPhone())
                .primaryContactEmail(request.getPrimaryContactEmail())
                .billingAddress(billingAddress)
                .deliveryAddresses(deliveryAddresses)
                .paymentTerms(request.getPaymentTerms())
                .category(request.getCategory())
                .creditLimit(request.getCreditLimit())
                .status(CustomerStatus.ACTIVE)
                .build();

        return customerRepository.save(customer);
    }


    @Override
    public Customer updateCustomer(Long id, CreateCustomerRequest request) {
        Customer customer = getCustomerById(id);


        customer.setCompanyName(request.getCompanyName());
        customer.setPrimaryContactName(request.getPrimaryContactName());
        customer.setPrimaryContactPhone(request.getPrimaryContactPhone());
        customer.setPrimaryContactEmail(request.getPrimaryContactEmail());
        customer.setBillingAddress(mapToAddress(request.getBillingAddress()));


        return customerRepository.save(customer);
    }

    @Override
    public Contract addContractToCustomer(CreateContractRequest request) {
        Customer customer = getCustomerById(request.getCustomerId());


        ServiceLevel serviceLevel = request.getServiceLevel();
        Contract contract = Contract.builder()
                .customer(customer)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .serviceLevel(serviceLevel)
                .discountRate(request.getDiscountRate())
                .build();

        Contract savedContract = contractRepository.save(contract);

        customer.setContract(savedContract);
        customerRepository.save(customer);

        return savedContract;
    }

    @Override
    @Transactional
    public Invoice processPayment(Long invoiceId, ProcessPaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        PaymentDetails paymentDetails = PaymentDetails.builder()
                .paymentDate(request.getPaymentDate())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .referenceNumber(request.getReferenceNumber())
                .build();

        invoice.recordPayment(paymentDetails);

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        checkCustomerOverdueStatus(updatedInvoice.getCustomer().getId());

        return updatedInvoice;
    }

    @Override
    @Transactional
    public void checkCustomerOverdueStatus(Long customerId) {
        Customer customer = getCustomerById(customerId);
        Double overdueAmount = invoiceRepository.calculateTotalAmountByStatus(customerId, InvoiceStatus.OVERDUE);

        if (overdueAmount > customer.getCreditLimit()) {
            if (customer.getStatus() == CustomerStatus.ACTIVE) {
                customer.suspendAccount();
                customerRepository.save(customer);
            }
        } else {
            if (customer.getStatus() == CustomerStatus.SUSPENDED) {
                customer.activateAccount();
                customerRepository.save(customer);
            }
        }
    }

    @Override
    @Transactional
    public Invoice generateInvoice(Long customerId, Long shipmentId, double amount) {
        Customer customer = getCustomerById(customerId);

        double finalAmount = customer.getCategory().applyDiscount(amount);
        double taxes = finalAmount * 0.19;
        double totalAmount = finalAmount + taxes;

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = customer.getPaymentTerms().calculateDueDate(issueDate);

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-" + shipmentId + "-" + System.currentTimeMillis() % 10000)
                .customer(customer)
                .shipmentId(shipmentId)
                .issueDate(issueDate)
                .dueDate(dueDate)
                .amount(finalAmount)
                .taxes(taxes)
                .totalAmount(totalAmount)
                .status(InvoiceStatus.PENDING)
                .build();

        return invoiceRepository.save(invoice);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesForCustomer(Long customerId) {
        // Folosim repository-ul pentru a găsi facturile
        return invoiceRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }


    private Address mapToAddress(AddressDto dto) {
        return Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .country(dto.getCountry())
                .build();
    }
}