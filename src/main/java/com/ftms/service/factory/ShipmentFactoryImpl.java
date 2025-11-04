package com.ftms.service.factory;

import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.common.Location;
import com.ftms.domain.customer.Customer;
import com.ftms.service.repository.IShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Shipment Factory Implementation
 */
@Component
public class ShipmentFactoryImpl implements IShipmentFactory {
    
    private static Logger logger = Logger.getLogger(ShipmentFactoryImpl.class.getName());
    
    @Autowired
    private IShipmentRepository shipmentRepository;

    @Override
    public Shipment buildShipment(String referenceNumber, Location pickupLocation,
                                  Location deliveryLocation, Date pickupDateTime,
                                  Date requestedDeliveryDateTime, Customer customer,
                                  String cargoDescription, Double weightKg, Double volumeCubicMeters) {
        
        Integer nextID = shipmentRepository.getNextID();
        
        Shipment shipment = new Shipment(referenceNumber, pickupLocation, deliveryLocation,
                pickupDateTime, requestedDeliveryDateTime, customer,
                cargoDescription, weightKg, volumeCubicMeters);
        
        shipment.setShipmentId(nextID);
        
        logger.info("Built shipment: " + shipment);
        return shipment;
    }

    @Override
    public Shipment toEntity(Shipment shipmentDTO) {
        Shipment entity = shipmentRepository.get(shipmentDTO);
        if (entity != null) {
            entity.setCargoDescription(shipmentDTO.getCargoDescription());
            entity.setWeightKg(shipmentDTO.getWeightKg());
            entity.setVolumeCubicMeters(shipmentDTO.getVolumeCubicMeters());
        }
        return entity;
    }

    @PostConstruct
    @Override
    public void initDomainServiceEntities() {
        logger.info(">> PostConstruct :: initDomainServiceEntities for Shipments");
        // Initialize sample data if needed
    }
}
