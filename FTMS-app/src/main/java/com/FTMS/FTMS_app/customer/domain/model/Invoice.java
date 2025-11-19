package com.FTMS.FTMS_app.customer.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*; // Builder, ToString, etc.

import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // <-- Esențial pentru generarea facturii în Service
@ToString(exclude = "customer") // <-- Previne încărcarea Lazy a clientului doar pentru un log
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private Long shipmentId; // Referință externă (Loose Coupling)

    private LocalDate issueDate;
    private LocalDate dueDate;

    @Min(0)
    private double amount;      // Valoarea serviciilor

    @Min(0)
    private double taxes;       // TVA

    @Min(0)
    private double totalAmount; // Total de plată

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "paymentDate", column = @Column(name = "payment_date")),
            @AttributeOverride(name = "amount", column = @Column(name = "payment_amount")), // Soluția critică
            @AttributeOverride(name = "paymentMethod", column = @Column(name = "payment_method")),
            @AttributeOverride(name = "referenceNumber", column = @Column(name = "payment_reference_number"))
    })
    private PaymentDetails paymentDetails;

    // --- Logica de Business ---

    public void recordPayment(PaymentDetails details) {
        // Folosim un epsilon mic pentru compararea double-urilor, e mai sigur
        double epsilon = 0.001;

        if (Math.abs(details.getAmount() - this.totalAmount) < epsilon) {
            this.status = InvoiceStatus.PAID;
        } else if (details.getAmount() < this.totalAmount) {
            this.status = InvoiceStatus.PARTIALLY_PAID;
        }
        // Dacă plătește mai mult, tot PAID rămâne (sau logică de creditare viitoare)

        this.paymentDetails = details;
    }

    public void markAsOverdue() {
        if (this.status == InvoiceStatus.PENDING && LocalDate.now().isAfter(dueDate)) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }
}