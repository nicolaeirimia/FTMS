package com.ftms.service.workflow;

import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.shipment.DeliveryConfirmation;
import com.ftms.domain.fleet.Vehicle;
import com.ftms.domain.fleet.Driver;
import com.ftms.service.validation.IValidationService;
import com.ftms.service.repository.IShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Workflow Service Implementation
 * Orchestrates shipment lifecycle workflows
 */
@Service
public class WorkflowServiceImpl implements IWorkflowService {
    
    private static Logger logger = Logger.getLogger(WorkflowServiceImpl.class.getName());
    
    @Autowired
    private IValidationService validationService;
    
    @Autowired
    private IShipmentRepository shipmentRepository;

    @Override
    public Shipment createShipment(Shipment shipment) throws Exception {
        logger.info("Creating shipment: " + shipment.getReferenceNumber());
        
        // Validate shipment
        validationService.validateShipmentWithException(shipment);
        
        // Save shipment
        shipmentRepository.add(shipment);
        
        logger.info("Shipment created successfully with ID: " + shipment.getShipmentId());
        return shipment;
    }

    @Override
    public Shipment assignResources(Shipment shipment, Vehicle vehicle, Driver driver) throws Exception {
        logger.info("Assigning resources to shipment: " + shipment.getReferenceNumber());
        
        // Validate assignment
        if (!validationService.canAssignToShipment(vehicle, driver, shipment)) {
            throw new Exception("Cannot assign resources to shipment - validation failed");
        }
        
        // Perform assignment
        shipment.assignResources(vehicle, driver);
        
        // Update vehicle and driver status
        vehicle.markAsInUse();
        driver.markAsOnRoute();
        
        logger.info("Resources assigned successfully");
        return shipment;
    }

    @Override
    public Shipment pickupShipment(Shipment shipment) throws Exception {
        logger.info("Picking up shipment: " + shipment.getReferenceNumber());
        
        if (!shipment.hasResourcesAssigned()) {
            throw new Exception("Cannot pickup shipment - no resources assigned");
        }
        
        shipment.markAsPickedUp();
        
        logger.info("Shipment picked up successfully");
        return shipment;
    }

    @Override
    public Shipment startTransit(Shipment shipment) throws Exception {
        logger.info("Starting transit for shipment: " + shipment.getReferenceNumber());
        
        shipment.markAsInTransit();
        
        logger.info("Shipment in transit");
        return shipment;
    }

    @Override
    public Shipment completeDelivery(Shipment shipment, DeliveryConfirmation confirmation) throws Exception {
        logger.info("Completing delivery for shipment: " + shipment.getReferenceNumber());
        
        if (confirmation == null) {
            throw new Exception("Delivery confirmation is required");
        }
        
        shipment.completeDelivery(confirmation);
        
        // Release resources
        if (shipment.getAssignedVehicle() != null) {
            shipment.getAssignedVehicle().makeAvailable();
        }
        if (shipment.getAssignedDriver() != null) {
            shipment.getAssignedDriver().makeAvailable();
        }
        
        logger.info("Delivery completed successfully");
        return shipment;
    }

    @Override
    public Shipment cancelShipment(Shipment shipment) throws Exception {
        logger.info("Canceling shipment: " + shipment.getReferenceNumber());
        
        shipment.cancel();
        
        // Release resources
        if (shipment.getAssignedVehicle() != null) {
            shipment.getAssignedVehicle().makeAvailable();
        }
        if (shipment.getAssignedDriver() != null) {
            shipment.getAssignedDriver().makeAvailable();
        }
        
        logger.info("Shipment canceled successfully");
        return shipment;
    }
}
