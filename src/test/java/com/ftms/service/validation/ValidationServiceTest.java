package com.ftms.service.validation;

import com.ftms.config.FTMSAppConfiguration;
import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.fleet.Vehicle;
import com.ftms.domain.fleet.Driver;
import com.ftms.domain.customer.Customer;
import com.ftms.domain.common.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test cases for Validation Service
 * Tests business rules and validation logic
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FTMSAppConfiguration.class})
public class ValidationServiceTest {
    
    @Autowired
    private IValidationService validationService;
    
    private Vehicle testVehicle;
    private Driver testDriver;
    private Customer testCustomer;
    private Shipment testShipment;

    @Before
    public void setUp() {
        // Setup test vehicle
        testVehicle = new Vehicle();
        testVehicle.setVehicleId(1);
        testVehicle.setRegistrationNumber("TEST-001");
        testVehicle.setMakeAndModel("Mercedes Actros");
        testVehicle.setVehicleType("box truck");
        testVehicle.setYearOfManufacture(2020);
        testVehicle.setMaxWeightKg(5000.0);
        testVehicle.setMaxVolumeCubicMeters(30.0);
        testVehicle.setStatus(VehicleStatus.AVAILABLE);
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        testVehicle.setInsuranceExpiryDate(cal.getTime());
        testVehicle.setRegistrationExpiryDate(cal.getTime());
        
        // Setup test driver
        testDriver = new Driver();
        testDriver.setDriverId(1);
        testDriver.setName("John Doe");
        testDriver.setPhone("+1234567890");
        testDriver.setEmail("john.doe@example.com");
        testDriver.setLicenseNumber("DL123456");
        testDriver.setLicenseType("CE");
        testDriver.setLicenseIssueDate(new Date());
        testDriver.setLicenseExpiryDate(cal.getTime());
        testDriver.setStatus(DriverStatus.AVAILABLE);
        
        // Setup test customer
        Address billingAddress = new Address("123 Business St", "Bucharest", "Ilfov", "010001", "Romania");
        testCustomer = new Customer("Test Company SRL", "RO12345678",
                                   "John Manager", "+40123456789",
                                   "manager@testcompany.ro", billingAddress);
        testCustomer.setCustomerId(1);
        
        // Setup test shipment
        Address pickupAddr = new Address("100 Pickup St", "Bucharest", "Ilfov", "010001", "Romania");
        Address deliveryAddr = new Address("200 Delivery Ave", "Cluj-Napoca", "Cluj", "400001", "Romania");
        Location pickup = new Location(pickupAddr, "Sender", "+40111111111");
        Location delivery = new Location(deliveryAddr, "Receiver", "+40222222222");
        
        testShipment = new Shipment();
        testShipment.setShipmentId(1);
        testShipment.setReferenceNumber("SHIP-001");
        testShipment.setPickupLocation(pickup);
        testShipment.setDeliveryLocation(delivery);
        testShipment.setCargoDescription("Test cargo");
        testShipment.setWeightKg(1000.0);
        testShipment.setVolumeCubicMeters(10.0);
        testShipment.setCustomer(testCustomer);
        
        Date now = new Date();
        testShipment.setPickupDateTime(now);
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        testShipment.setRequestedDeliveryDateTime(cal.getTime());
    }

    @Test
    public void testValidateVehicle_Valid() {
        System.out.println("Test 1: Validate valid vehicle");
        Set<String> errors = validationService.validateVehicle(testVehicle);
        assertTrue("Valid vehicle should have no errors", errors.isEmpty());
    }

    @Test
    public void testValidateVehicle_MissingRegistration() {
        System.out.println("Test 2: Validate vehicle with missing registration");
        testVehicle.setRegistrationNumber("");
        Set<String> errors = validationService.validateVehicle(testVehicle);
        assertFalse("Vehicle with missing registration should have errors", errors.isEmpty());
    }

    @Test
    public void testValidateVehicle_ExpiredInsurance() {
        System.out.println("Test 3: Validate vehicle with expired insurance");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        testVehicle.setInsuranceExpiryDate(cal.getTime());
        
        Set<String> errors = validationService.validateVehicle(testVehicle);
        assertFalse("Vehicle with expired insurance should have errors", errors.isEmpty());
        assertTrue("Should contain insurance expiry message", 
                  errors.stream().anyMatch(e -> e.contains("insurance")));
    }

    @Test
    public void testValidateDriver_Valid() {
        System.out.println("Test 4: Validate valid driver");
        Set<String> errors = validationService.validateDriver(testDriver);
        assertTrue("Valid driver should have no errors", errors.isEmpty());
    }

    @Test
    public void testValidateDriver_ExpiredLicense() {
        System.out.println("Test 5: Validate driver with expired license");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        testDriver.setLicenseExpiryDate(cal.getTime());
        
        Set<String> errors = validationService.validateDriver(testDriver);
        assertFalse("Driver with expired license should have errors", errors.isEmpty());
        assertTrue("Should contain license expiry message",
                  errors.stream().anyMatch(e -> e.contains("license")));
    }

    @Test
    public void testValidateCustomer_Valid() {
        System.out.println("Test 6: Validate valid customer");
        Set<String> errors = validationService.validateCustomer(testCustomer);
        assertTrue("Valid customer should have no errors", errors.isEmpty());
    }

    @Test
    public void testCanAssignToShipment_Valid() {
        System.out.println("Test 7: Can assign valid resources to shipment");
        boolean canAssign = validationService.canAssignToShipment(testVehicle, testDriver, testShipment);
        assertTrue("Should be able to assign valid vehicle and driver", canAssign);
    }

    @Test
    public void testCanAssignToShipment_InsufficientCapacity() {
        System.out.println("Test 8: Cannot assign - insufficient capacity");
        testShipment.setWeightKg(10000.0); // Exceeds vehicle capacity
        
        boolean canAssign = validationService.canAssignToShipment(testVehicle, testDriver, testShipment);
        assertFalse("Should not assign when vehicle capacity exceeded", canAssign);
    }

    @Test
    public void testCanAssignToShipment_VehicleNotAvailable() {
        System.out.println("Test 9: Cannot assign - vehicle not available");
        testVehicle.setStatus(VehicleStatus.IN_USE);
        
        boolean canAssign = validationService.canAssignToShipment(testVehicle, testDriver, testShipment);
        assertFalse("Should not assign unavailable vehicle", canAssign);
    }

    @Test
    public void testCanAssignToShipment_DriverNotAvailable() {
        System.out.println("Test 10: Cannot assign - driver not available");
        testDriver.setStatus(DriverStatus.ON_ROUTE);
        
        boolean canAssign = validationService.canAssignToShipment(testVehicle, testDriver, testShipment);
        assertFalse("Should not assign unavailable driver", canAssign);
    }

    @Test
    public void testValidateShipmentWithException_InvalidDeliveryDate() {
        System.out.println("Test 11: Validate shipment with invalid delivery date");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        testShipment.setRequestedDeliveryDateTime(cal.getTime()); // Before pickup date
        
        try {
            validationService.validateShipmentWithException(testShipment);
            fail("Should throw exception for invalid delivery date");
        } catch (Exception e) {
            assertTrue("Exception message should mention delivery date",
                      e.getMessage().contains("Delivery date") || e.getMessage().contains("after pickup"));
        }
    }

    @Test
    public void testValidateShipmentWithException_InactiveCustomer() {
        System.out.println("Test 12: Validate shipment with inactive customer");
        testCustomer.setStatus(CustomerStatus.SUSPENDED);
        
        try {
            validationService.validateShipmentWithException(testShipment);
            fail("Should throw exception for inactive customer");
        } catch (Exception e) {
            assertTrue("Exception message should mention customer status",
                      e.getMessage().contains("Customer") || e.getMessage().contains("active"));
        }
    }
}
