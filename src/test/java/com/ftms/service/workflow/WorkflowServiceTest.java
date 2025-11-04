package com.ftms.service.workflow;

import com.ftms.config.FTMSAppConfiguration;
import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.shipment.DeliveryConfirmation;
import com.ftms.domain.fleet.Vehicle;
import com.ftms.domain.fleet.Driver;
import com.ftms.domain.customer.Customer;
import com.ftms.domain.common.*;
import com.ftms.service.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test cases for Workflow Service
 * Tests complete shipment lifecycle workflows
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FTMSAppConfiguration.class})
public class WorkflowServiceTest {
    
    @Autowired
    private IWorkflowService workflowService;
    
    @Autowired
    private IShipmentRepository shipmentRepository;
    
    @Autowired
    private IVehicleRepository vehicleRepository;
    
    @Autowired
    private IDriverRepository driverRepository;
    
    @Autowired
    private ICustomerRepository customerRepository;
    
    private Vehicle testVehicle;
    private Driver testDriver;
    private Customer testCustomer;
    private Shipment testShipment;

    @Before
    public void setUp() {
        // Clear repositories before each test
        shipmentRepository.toCollection().forEach(shipmentRepository::remove);
        vehicleRepository.toCollection().forEach(vehicleRepository::remove);
        driverRepository.toCollection().forEach(driverRepository::remove);
        customerRepository.toCollection().forEach(customerRepository::remove);
        
        // Setup test customer
        Address billingAddress = new Address("123 Business St", "Bucharest", "Ilfov", "010001", "Romania");
        testCustomer = new Customer("Test Company SRL", "RO12345678",
                                   "John Manager", "+40123456789",
                                   "manager@testcompany.ro", billingAddress);
        customerRepository.add(testCustomer);
        
        // Setup test vehicle
        testVehicle = new Vehicle("TEST-001", "Mercedes Actros", "box truck",
                                 2020, 5000.0, 30.0);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        testVehicle.setInsuranceExpiryDate(cal.getTime());
        testVehicle.setRegistrationExpiryDate(cal.getTime());
        testVehicle.setFuelType("Diesel");
        testVehicle.setCurrentMileage(50000);
        vehicleRepository.add(testVehicle);
        
        // Setup test driver
        testDriver = new Driver("John Doe", "+40123456789", "john@example.com",
                              "DL123456", "CE", new Date(), cal.getTime());
        testDriver.setDateOfEmployment(new Date());
        testDriver.setEmergencyContact("+40999999999");
        driverRepository.add(testDriver);
        
        // Setup test shipment
        Address pickupAddr = new Address("100 Pickup St", "Bucharest", "Ilfov", "010001", "Romania");
        Address deliveryAddr = new Address("200 Delivery Ave", "Cluj-Napoca", "Cluj", "400001", "Romania");
        Location pickup = new Location(pickupAddr, "Sender Name", "+40111111111");
        Location delivery = new Location(deliveryAddr, "Receiver Name", "+40222222222");
        
        testShipment = new Shipment("SHIP-TEST-001", pickup, delivery,
                                   new Date(), cal.getTime(), testCustomer,
                                   "Test cargo - electronics", 1000.0, 10.0);
        testShipment.setAdditionalNotes("Handle with care");
    }

    @Test
    public void testCreateShipment() throws Exception {
        System.out.println("Test 1: Create shipment");
        
        Shipment created = workflowService.createShipment(testShipment);
        
        assertNotNull("Created shipment should not be null", created);
        assertNotNull("Shipment ID should be assigned", created.getShipmentId());
        assertEquals("Status should be PENDING", ShipmentStatus.PENDING, created.getStatus());
        assertEquals("Reference number should match", "SHIP-TEST-001", created.getReferenceNumber());
        
        // Verify it's in repository
        Shipment fromRepo = shipmentRepository.getById(created.getShipmentId());
        assertNotNull("Shipment should be in repository", fromRepo);
        
        System.out.println("  ✓ Shipment created with ID: " + created.getShipmentId());
    }

    @Test
    public void testAssignResources() throws Exception {
        System.out.println("Test 2: Assign resources to shipment");
        
        // Create shipment first
        workflowService.createShipment(testShipment);
        
        // Verify initial status
        assertEquals("Initial vehicle status", VehicleStatus.AVAILABLE, testVehicle.getStatus());
        assertEquals("Initial driver status", DriverStatus.AVAILABLE, testDriver.getStatus());
        
        // Assign resources
        Shipment assigned = workflowService.assignResources(testShipment, testVehicle, testDriver);
        
        assertEquals("Status should be SCHEDULED", ShipmentStatus.SCHEDULED, assigned.getStatus());
        assertNotNull("Vehicle should be assigned", assigned.getAssignedVehicle());
        assertNotNull("Driver should be assigned", assigned.getAssignedDriver());
        assertEquals("Assigned vehicle should match", testVehicle.getVehicleId(), 
                    assigned.getAssignedVehicle().getVehicleId());
        assertEquals("Assigned driver should match", testDriver.getDriverId(),
                    assigned.getAssignedDriver().getDriverId());
        
        // Verify resource status changed
        assertEquals("Vehicle status should be IN_USE", VehicleStatus.IN_USE, testVehicle.getStatus());
        assertEquals("Driver status should be ON_ROUTE", DriverStatus.ON_ROUTE, testDriver.getStatus());
        
        System.out.println("  ✓ Resources assigned successfully");
    }

    @Test
    public void testPickupShipment() throws Exception {
        System.out.println("Test 3: Pickup shipment");
        
        // Create and assign resources
        workflowService.createShipment(testShipment);
        workflowService.assignResources(testShipment, testVehicle, testDriver);
        
        // Pickup shipment
        Shipment pickedUp = workflowService.pickupShipment(testShipment);
        
        assertEquals("Status should be PICKED_UP", ShipmentStatus.PICKED_UP, pickedUp.getStatus());
        System.out.println("  ✓ Shipment picked up");
    }

    @Test
    public void testStartTransit() throws Exception {
        System.out.println("Test 4: Start transit");
        
        // Create, assign, and pickup
        workflowService.createShipment(testShipment);
        workflowService.assignResources(testShipment, testVehicle, testDriver);
        workflowService.pickupShipment(testShipment);
        
        // Start transit
        Shipment inTransit = workflowService.startTransit(testShipment);
        
        assertEquals("Status should be IN_TRANSIT", ShipmentStatus.IN_TRANSIT, inTransit.getStatus());
        System.out.println("  ✓ Shipment in transit");
    }

    @Test
    public void testCompleteDelivery() throws Exception {
        System.out.println("Test 5: Complete delivery - full lifecycle");
        
        // Full workflow: create → assign → pickup → transit → deliver
        workflowService.createShipment(testShipment);
        workflowService.assignResources(testShipment, testVehicle, testDriver);
        workflowService.pickupShipment(testShipment);
        workflowService.startTransit(testShipment);
        
        // Create delivery confirmation
        DeliveryConfirmation confirmation = new DeliveryConfirmation(
            new Date(), "Jane Receiver", "SIGNATURE-12345"
        );
        confirmation.setPhotoDocumentation("photo_url_123.jpg");
        
        // Complete delivery
        Shipment completed = workflowService.completeDelivery(testShipment, confirmation);
        
        assertEquals("Status should be DELIVERED", ShipmentStatus.DELIVERED, completed.getStatus());
        assertNotNull("Delivery confirmation should be set", completed.getDeliveryConfirmation());
        assertNotNull("Actual delivery date should be set", completed.getActualDeliveryDateTime());
        assertEquals("Recipient should match", "Jane Receiver", 
                    completed.getDeliveryConfirmation().getRecipientName());
        
        // Verify resources are released
        assertEquals("Vehicle should be available", VehicleStatus.AVAILABLE, testVehicle.getStatus());
        assertEquals("Driver should be available", DriverStatus.AVAILABLE, testDriver.getStatus());
        
        System.out.println("  ✓ Delivery completed - full lifecycle successful");
    }

    @Test
    public void testCancelShipment_AfterAssignment() throws Exception {
        System.out.println("Test 6: Cancel shipment after assignment");
        
        // Create and assign
        workflowService.createShipment(testShipment);
        workflowService.assignResources(testShipment, testVehicle, testDriver);
        
        // Cancel
        Shipment canceled = workflowService.cancelShipment(testShipment);
        
        assertEquals("Status should be CANCELED", ShipmentStatus.CANCELED, canceled.getStatus());
        
        // Verify resources are released
        assertEquals("Vehicle should be available", VehicleStatus.AVAILABLE, testVehicle.getStatus());
        assertEquals("Driver should be available", DriverStatus.AVAILABLE, testDriver.getStatus());
        
        System.out.println("  ✓ Shipment canceled, resources released");
    }

    @Test
    public void testCancelShipment_BeforeAssignment() throws Exception {
        System.out.println("Test 7: Cancel shipment before assignment");
        
        // Create only
        workflowService.createShipment(testShipment);
        
        // Cancel
        Shipment canceled = workflowService.cancelShipment(testShipment);
        
        assertEquals("Status should be CANCELED", ShipmentStatus.CANCELED, canceled.getStatus());
        assertNull("No vehicle should be assigned", canceled.getAssignedVehicle());
        assertNull("No driver should be assigned", canceled.getAssignedDriver());
        
        System.out.println("  ✓ Shipment canceled before assignment");
    }

    @Test(expected = Exception.class)
    public void testPickupWithoutAssignment_ShouldFail() throws Exception {
        System.out.println("Test 8: Pickup without assignment should fail");
        
        // Create shipment but don't assign resources
        workflowService.createShipment(testShipment);
        
        // Try to pickup without assignment - should throw exception
        workflowService.pickupShipment(testShipment);
        
        fail("Should have thrown exception for pickup without assignment");
    }

    @Test(expected = Exception.class)
    public void testAssignToInactiveCustomer_ShouldFail() throws Exception {
        System.out.println("Test 9: Create shipment for inactive customer should fail");
        
        // Suspend customer
        testCustomer.suspend();
        
        // Try to create shipment - should fail validation
        workflowService.createShipment(testShipment);
        
        fail("Should have thrown exception for inactive customer");
    }

    @Test
    public void testMultipleShipments_ResourcesReleased() throws Exception {
        System.out.println("Test 10: Multiple shipments - resources properly released");
        
        // First shipment - complete full cycle
        workflowService.createShipment(testShipment);
        workflowService.assignResources(testShipment, testVehicle, testDriver);
        workflowService.pickupShipment(testShipment);
        workflowService.startTransit(testShipment);
        
        DeliveryConfirmation conf1 = new DeliveryConfirmation(new Date(), "Receiver 1", "SIG-001");
        workflowService.completeDelivery(testShipment, conf1);
        
        // Verify resources are available
        assertTrue("Vehicle should be available", testVehicle.isAvailable());
        assertTrue("Driver should be available", testDriver.isAvailable());
        
        // Second shipment - should be able to use same resources
        Address pickupAddr2 = new Address("300 Street", "Timisoara", "Timis", "300001", "Romania");
        Address deliveryAddr2 = new Address("400 Avenue", "Iasi", "Iasi", "700001", "Romania");
        Location pickup2 = new Location(pickupAddr2, "Sender 2", "+40333333333");
        Location delivery2 = new Location(deliveryAddr2, "Receiver 2", "+40444444444");
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        
        Shipment shipment2 = new Shipment("SHIP-TEST-002", pickup2, delivery2,
                                         new Date(), cal.getTime(), testCustomer,
                                         "Second shipment cargo", 800.0, 8.0);
        
        workflowService.createShipment(shipment2);
        workflowService.assignResources(shipment2, testVehicle, testDriver);
        
        assertEquals("Second shipment should be SCHEDULED", ShipmentStatus.SCHEDULED, shipment2.getStatus());
        assertEquals("Vehicle should be IN_USE again", VehicleStatus.IN_USE, testVehicle.getStatus());
        assertEquals("Driver should be ON_ROUTE again", DriverStatus.ON_ROUTE, testDriver.getStatus());
        
        System.out.println("  ✓ Resources successfully reused for second shipment");
    }
}
