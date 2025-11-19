package com.FTMS.FTMS_app.shipment.domain.model;

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
@ToString(exclude = "shipment") // <-- CRITIC: Rupe bucla infinită cu Shipment
public class DeliveryConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false) // Cheia străină este aici
    private Shipment shipment;

    private LocalDateTime actualDeliveryDateTime;

    private String recipientName;

    private String recipientSignature; // URL sau Base64 string

    private String issuesOrDamages; // Null dacă totul e ok

    private String photoDocumentationUrl; // URL către cloud storage (S3/MinIO)

    // Constructorul manual nu mai este necesar dacă ai @Builder,
    // dar îl poți păstra dacă îți place.
}