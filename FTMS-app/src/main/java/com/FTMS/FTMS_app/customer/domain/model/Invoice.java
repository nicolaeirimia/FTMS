package com.FTMS.FTMS_app.customer.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*; // Builder, ToString, etc.
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "customer")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Customer customer;

    @Column(nullable = false)
    private Long shipmentId;

    private LocalDate issueDate;
    private LocalDate dueDate;

    @Min(0)
    private double amount;

    @Min(0)
    private double taxes;

    @Min(0)
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



    public void recordPayment(PaymentDetails details) {

        double epsilon = 0.001;

        if (Math.abs(details.getAmount() - this.totalAmount) < epsilon) {
            this.status = InvoiceStatus.PAID;
        } else if (details.getAmount() < this.totalAmount) {
            this.status = InvoiceStatus.PARTIALLY_PAID;
        }


        this.paymentDetails = details;
    }

    public void markAsOverdue() {
        if (this.status == InvoiceStatus.PENDING && LocalDate.now().isAfter(dueDate)) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }
}