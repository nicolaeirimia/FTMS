package com.FTMS.FTMS_app.shipment.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference; // <-- IMPORT NOU
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_confirmations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "shipment")
public class DeliveryConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    @JsonBackReference
    private Shipment shipment;

    private LocalDateTime actualDeliveryDateTime;

    private String recipientName;

    private String recipientSignature;

    private String issuesOrDamages;

    private String photoDocumentationUrl;
}