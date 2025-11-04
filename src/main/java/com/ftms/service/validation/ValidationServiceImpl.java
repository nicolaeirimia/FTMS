package com.ftms.service.validation;

import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.fleet.Vehicle;
import com.ftms.domain.fleet.Driver;
import com.ftms.domain.customer.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Validation Service Implementation
 */
@Service
public class ValidationServiceImpl implements IValidationService {
    
    private static Logger logger = Logger.getLogger(ValidationServiceImpl.class.getName());
    
    @Autowired
    private Validator validator;

    @Override
    public Set<String> validateShipment(Shipment shipment) {
        Set<ConstraintViolation<Shipment>> violations = validator.validate(shipment);
        logger.info("Shipment validation violations: " + violations.size());
        
        return violations.stream()
                .map(v -> v.getMessage() + " (" + v.getInvalidValue() + ")")
                .collect(Collectors.toSet());
    }

    @Override
    public void validateShipmentWithException(Shipment shipment) throws Exception {
        Set<String> violations = validateShipment(shipment);
        
        // Additional business rule validations
        if (shipment.getPickupDateTime() != null && 
            shipment.getRequestedDeliveryDateTime() != null) {
            if (shipment.getRequestedDeliveryDateTime().before(shipment.getPickupDateTime())) {
                violations.add("Delivery date must be after pickup date");
            }
        }
        
        if (shipment.getCustomer() != null && !shipment.getCustomer().isActive()) {
            violations.add("Customer account is not active");
        }
        
        if (!violations.isEmpty()) {
            String message = String.join(", ", violations);
            throw new Exception("Shipment validation failed: " + message);
        }
    }

    @Override
    public Set<String> validateVehicle(Vehicle vehicle) {
        Set<ConstraintViolation<Vehicle>> violations = validator.validate(vehicle);
        
        // Additional validations
        Set<String> errors = violations.stream()
                .map(v -> v.getMessage())
                .collect(Collectors.toSet());
        
        if (vehicle.getInsuranceExpiryDate() != null && 
            vehicle.getInsuranceExpiryDate().before(new Date())) {
            errors.add("Vehicle insurance has expired");
        }
        
        if (vehicle.getRegistrationExpiryDate() != null && 
            vehicle.getRegistrationExpiryDate().before(new Date())) {
            errors.add("Vehicle registration has expired");
        }
        
        return errors;
    }

    @Override
    public Set<String> validateDriver(Driver driver) {
        Set<ConstraintViolation<Driver>> violations = validator.validate(driver);
        
        Set<String> errors = violations.stream()
                .map(v -> v.getMessage())
                .collect(Collectors.toSet());
        
        if (!driver.hasValidLicense()) {
            errors.add("Driver license is expired");
        }
        
        return errors;
    }

    @Override
    public Set<String> validateCustomer(Customer customer) {
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        
        return violations.stream()
                .map(v -> v.getMessage())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean canAssignToShipment(Vehicle vehicle, Driver driver, Shipment shipment) {
        // Check vehicle capacity
        if (!vehicle.canHandle(shipment.getWeightKg(), shipment.getVolumeCubicMeters())) {
            logger.warning("Vehicle cannot handle cargo capacity");
            return false;
        }
        
        // Check vehicle availability
        if (!vehicle.isAvailable()) {
            logger.warning("Vehicle is not available");
            return false;
        }
        
        // Check driver availability
        if (!driver.isAvailable()) {
            logger.warning("Driver is not available");
            return false;
        }
        
        // Check driver license
        if (!driver.hasValidLicense()) {
            logger.warning("Driver license is not valid");
            return false;
        }
        
        // Check if driver can drive the vehicle
        if (!driver.canDriveVehicle(vehicle)) {
            logger.warning("Driver cannot drive this vehicle type");
            return false;
        }
        
        return true;
    }
}
