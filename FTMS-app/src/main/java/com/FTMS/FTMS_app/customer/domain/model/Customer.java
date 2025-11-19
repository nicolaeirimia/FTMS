package com.FTMS.FTMS_app.customer.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // <-- Foarte util pentru teste
@ToString(exclude = "contract") // <-- CRITIC: Rupe bucla infinită
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Column(nullable = false)
    private String companyName;

    @NotBlank(message = "Tax ID is required")
    @Column(unique = true, nullable = false)
    private String taxIdNumber; // CUI

    @Column(unique = true)
    private String registrationNumber; // Nr. Reg. Com.

    // Contact Info Simplificat (E OK să fie plat aici)
    @NotBlank
    private String primaryContactName;

    @NotBlank
    private String primaryContactPhone;

    @Email
    @Column(unique = true)
    private String primaryContactEmail;

    @Embedded // Adresa de facturare
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "billing_zip")),
            @AttributeOverride(name = "country", column = @Column(name = "billing_country"))
    })
    private Address billingAddress;

    @Builder.Default // <-- CRITIC: Asigură că lista nu e null când folosim Builder
    @ElementCollection // Hibernate creează tabelul 'customer_delivery_addresses'
    @CollectionTable(
            name = "customer_delivery_addresses",
            joinColumns = @JoinColumn(name = "customer_id")
    )
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

    public boolean canPlaceNewShipment() {
        return this.status == CustomerStatus.ACTIVE;
    }

    public void suspendAccount() {
        this.status = CustomerStatus.SUSPENDED;
    }

    public void activateAccount() {
        if (this.status == CustomerStatus.SUSPENDED) {
            this.status = CustomerStatus.ACTIVE;
        }
    }

    public void addDeliveryAddress(Address address) {
        if (this.deliveryAddresses == null) {
            this.deliveryAddresses = new ArrayList<>();
        }
        this.deliveryAddresses.add(address);
    }

    // Helper pentru a seta contractul și a menține relația bidirecțională
    public void setContract(Contract contract) {
        this.contract = contract;
        if (contract != null && contract.getCustomer() != this) {
            contract.setCustomer(this);
        }
    }
}