package com.ftms.service.audit;

import com.ftms.domain.shipment.Shipment;

/**
 * Audit Service Interface
 * Tracks changes and maintains audit trail
 */
public interface IAuditService {
    void logShipmentCreation(Shipment shipment);
    void logShipmentStatusChange(Shipment shipment, String oldStatus, String newStatus);
    void logResourceAssignment(Shipment shipment, Integer vehicleId, Integer driverId);
    void logDeliveryCompletion(Shipment shipment);
}
