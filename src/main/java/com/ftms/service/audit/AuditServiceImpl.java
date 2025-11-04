package com.ftms.service.audit;

import com.ftms.domain.shipment.Shipment;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Audit Service Implementation
 */
@Service
public class AuditServiceImpl implements IAuditService {
    
    private static Logger logger = Logger.getLogger(AuditServiceImpl.class.getName());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void logShipmentCreation(Shipment shipment) {
        String logEntry = String.format("[%s] SHIPMENT_CREATED | ID: %d | Ref: %s | Customer: %s",
                dateFormat.format(new Date()),
                shipment.getShipmentId(),
                shipment.getReferenceNumber(),
                shipment.getCustomer() != null ? shipment.getCustomer().getCompanyName() : "N/A");
        
        logger.info(logEntry);
    }

    @Override
    public void logShipmentStatusChange(Shipment shipment, String oldStatus, String newStatus) {
        String logEntry = String.format("[%s] STATUS_CHANGE | ID: %d | Ref: %s | %s -> %s",
                dateFormat.format(new Date()),
                shipment.getShipmentId(),
                shipment.getReferenceNumber(),
                oldStatus,
                newStatus);
        
        logger.info(logEntry);
    }

    @Override
    public void logResourceAssignment(Shipment shipment, Integer vehicleId, Integer driverId) {
        String logEntry = String.format("[%s] RESOURCE_ASSIGNMENT | Shipment: %d | Vehicle: %d | Driver: %d",
                dateFormat.format(new Date()),
                shipment.getShipmentId(),
                vehicleId,
                driverId);
        
        logger.info(logEntry);
    }

    @Override
    public void logDeliveryCompletion(Shipment shipment) {
        String logEntry = String.format("[%s] DELIVERY_COMPLETED | ID: %d | Ref: %s | Delivered: %s",
                dateFormat.format(new Date()),
                shipment.getShipmentId(),
                shipment.getReferenceNumber(),
                shipment.getActualDeliveryDateTime() != null ? 
                    dateFormat.format(shipment.getActualDeliveryDateTime()) : "N/A");
        
        logger.info(logEntry);
    }
}
