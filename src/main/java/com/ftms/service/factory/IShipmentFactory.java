package com.ftms.service.factory;

import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.common.Location;
import com.ftms.domain.customer.Customer;
import java.util.Date;

/**
 * Shipment Factory Interface
 * Creates and initializes Shipment aggregates
 */
public interface IShipmentFactory {
    Shipment buildShipment(String referenceNumber, Location pickupLocation, 
                          Location deliveryLocation, Date pickupDateTime, 
                          Date requestedDeliveryDateTime, Customer customer,
                          String cargoDescription, Double weightKg, Double volumeCubicMeters);
    
    Shipment toEntity(Shipment shipmentDTO);
    void initDomainServiceEntities();
}
