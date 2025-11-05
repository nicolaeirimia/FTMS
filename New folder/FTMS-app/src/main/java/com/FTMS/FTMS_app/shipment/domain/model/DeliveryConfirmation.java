package com.FTMS.FTMS_app.shipment.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_confirmations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false) // Legătura inversă
    private Shipment shipment;

    private LocalDateTime actualDeliveryDateTime;
    private String recipientName;
    private String recipientSignature; // Ar putea fi o cale către o imagine
    private String issuesOrDamages;
    private String photoDocumentationUrl; // Cale către poza

    public DeliveryConfirmation(Shipment shipment, LocalDateTime actualDeliveryDateTime, String recipientName, String recipientSignature, String issuesOrDamages, String photoDocumentationUrl) {
        this.shipment = shipment;
        this.actualDeliveryDateTime = actualDeliveryDateTime;
        this.recipientName = recipientName;
        this.recipientSignature = recipientSignature;
        this.issuesOrDamages = issuesOrDamages;
        this.photoDocumentationUrl = photoDocumentationUrl;
    }
}