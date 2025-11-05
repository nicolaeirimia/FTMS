package com.FTMS.FTMS_app.customer.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true, nullable = false)
    private String taxIdNumber; // CUI

    @Column(unique = true)
    private String registrationNumber; // Nr. Reg. Com.

    // Presupunem că folosim un VO ContactInfo similar cu cel din fleet
    // Dar pentru a fi simplu, folosim câmpuri simple
    private String primaryContactName;
    private String primaryContactPhone;
    @Column(unique = true)
    private String primaryContactEmail;

    @Embedded // Adresa de facturare
    @AttributeOverride(name = "street", column = @Column(name = "billing_street"))
    @AttributeOverride(name = "city", column = @Column(name = "billing_city"))
    // ... pot fi redenumite toate câmpurile dacă e nevoie
    private Address billingAddress;

    @ElementCollection // O listă de adrese embeddable (Value Objects)
    @CollectionTable(name = "customer_delivery_addresses", joinColumns = @JoinColumn(name = "customer_id"))
    private List<Address> deliveryAddresses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PaymentTerms paymentTerms;

    @Enumerated(EnumType.STRING)
    private CustomerCategory category;

    private double creditLimit;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Contract contract;

    // --- Logica de Business ---

    /**
     * Verifică dacă clientul poate plasa o comandă nouă.
     */
    public boolean canPlaceNewShipment() {
        // Cerință: "Customers with overdue invoices exceeding their credit limit... new shipment requests.http are blocked"
        // Logica aceasta (verificarea facturilor) va fi probabil în CustomerService,
        // deoarece Customer nu are acces direct la InvoiceRepository.
        // Aici verificăm doar statusul.
        return this.status == CustomerStatus.ACTIVE;
    }

    /**
     * Suspendă contul clientului.
     */
    public void suspendAccount() {
        this.status = CustomerStatus.SUSPENDED;
    }

    /**
     * Reactivează contul clientului.
     */
    public void activateAccount() {
        if (this.status == CustomerStatus.SUSPENDED) {
            this.status = CustomerStatus.ACTIVE;
        }
    }

    public void addDeliveryAddress(Address address) {
        this.deliveryAddresses.add(address);
    }
}