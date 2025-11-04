package com.ftms.service.repository;

import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.common.ShipmentStatus;
import java.util.Collection;

/**
 * Shipment Repository Interface
 */
public interface IShipmentRepository extends IBaseRepository<Shipment, Integer> {
    Collection<Shipment> findByStatus(ShipmentStatus status);
    Collection<Shipment> findByCustomerId(Integer customerId);
    Shipment findByReferenceNumber(String referenceNumber);
}
