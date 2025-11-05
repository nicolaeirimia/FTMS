package com.FTMS.FTMS_app.shipment.domain.model;

public enum ShipmentStatus {
    PENDING,     // La creare
    SCHEDULED,   // Vehicul și șofer alocați
    PICKED_UP,   // Ridicat de la client
    IN_TRANSIT,  // Pe drum
    DELIVERED,   // Livrat
    CANCELED     // Anulat
}