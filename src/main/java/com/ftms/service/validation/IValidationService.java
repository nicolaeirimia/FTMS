package com.ftms.service.validation;

import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.fleet.Vehicle;
import com.ftms.domain.fleet.Driver;
import com.ftms.domain.customer.Customer;
import java.util.Set;

/**
 * Validation Service Interface
 * Validates business rules and constraints
 */
public interface IValidationService {
    Set<String> validateShipment(Shipment shipment);
    void validateShipmentWithException(Shipment shipment) throws Exception;
    
    Set<String> validateVehicle(Vehicle vehicle);
    Set<String> validateDriver(Driver driver);
    Set<String> validateCustomer(Customer customer);
    
    boolean canAssignToShipment(Vehicle vehicle, Driver driver, Shipment shipment);
}
