package com.FTMS.FTMS_app.customer.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private Long shipmentId; // Referință către transportul facturat

    private LocalDate issueDate;
    private LocalDate dueDate;

    private double amount;
    private double taxes;
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "paymentDate", column = @Column(name = "payment_date")),
            @AttributeOverride(name = "amount", column = @Column(name = "payment_amount")),
            @AttributeOverride(name = "paymentMethod", column = @Column(name = "payment_method")),
            @AttributeOverride(name = "referenceNumber", column = @Column(name = "payment_reference_number"))
    })
    private PaymentDetails paymentDetails;

    // --- Logica de Business ---

    /**
     * Marchează factura ca fiind plătită.
     */
    public void recordPayment(PaymentDetails details) {
        if (details.getAmount() == this.totalAmount) {
            this.status = InvoiceStatus.PAID;
        } else if (details.getAmount() < this.totalAmount) {
            // Aici logica de business poate dicta dacă acceptăm plăți parțiale
            this.status = InvoiceStatus.PARTIALLY_PAID;
        }
        this.paymentDetails = details;
    }

    /**
     * Marchează factura ca fiind restantă (overdue).
     * Acest lucru va fi apelat de un proces extern (un "job" programat).
     */
    public void markAsOverdue() {
        if (this.status == InvoiceStatus.PENDING && LocalDate.now().isAfter(dueDate)) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }
}