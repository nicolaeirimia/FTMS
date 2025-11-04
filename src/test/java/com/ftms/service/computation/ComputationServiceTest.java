package com.ftms.service.computation;

import com.ftms.config.FTMSAppConfiguration;
import com.ftms.domain.common.Address;
import com.ftms.domain.common.Location;
import com.ftms.domain.customer.Customer;
import com.ftms.domain.shipment.Shipment;
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
 * Test cases for Computation Service
 * Tests distance, cost, and capacity calculations
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FTMSAppConfiguration.class})
public class ComputationServiceTest {
    
    @Autowired
    private IComputationService computationService;
    
    private Address address1;
    private Address address2;
    private Address address3;
    private Shipment testShipment;
    private Customer testCustomer;

    @Before
    public void setUp() {
        // Setup addresses
        address1 = new Address("123 Main St", "Bucharest", "Ilfov", "010001", "Romania");
        address2 = new Address("456 Oak Ave", "Cluj-Napoca", "Cluj", "400001", "Romania");
        address3 = new Address("789 Pine Rd", "Berlin", "Berlin", "10115", "Germany");
        
        // Setup customer
        Address billingAddress = new Address("100 Business Ave", "Bucharest", "Ilfov", "010001", "Romania");
        testCustomer = new Customer("Test Logistics SRL", "RO87654321",
                                   "Jane Manager", "+40987654321",
                                   "jane@testlogistics.ro", billingAddress);
        testCustomer.setCustomerId(1);
        
        // Setup shipment
        Location pickup = new Location(address1, "John Sender", "+40123456789");
        Location delivery = new Location(address2, "Jane Receiver", "+40987654321");
        
        testShipment = new Shipment();
        testShipment.setReferenceNumber("SHIP-TEST-001");
        testShipment.setPickupLocation(pickup);
        testShipment.setDeliveryLocation(delivery);
        testShipment.setWeightKg(500.0);
        testShipment.setVolumeCubicMeters(5.0);
        testShipment.setCustomer(testCustomer);
        
        Date now = new Date();
        testShipment.setPickupDateTime(now);
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        testShipment.setRequestedDeliveryDateTime(cal.getTime());
        testShipment.setCargoDescription("Test cargo");
    }

    @Test
    public void testCalculateDistance_SameCity() {
        System.out.println("Test 1: Calculate distance within same city");
        Address addr1 = new Address("Street 1", "Bucharest", "Ilfov", "010001", "Romania");
        Address addr2 = new Address("Street 2", "Bucharest", "Ilfov", "010002", "Romania");
        
        Double distance = computationService.calculateDistance(addr1, addr2);
        assertNotNull("Distance should not be null", distance);
        assertTrue("Distance should be positive", distance > 0);
        assertTrue("Same city distance should be relatively small", distance < 500);
    }

    @Test
    public void testCalculateDistance_DifferentCities() {
        System.out.println("Test 2: Calculate distance between different cities");
        Double distance = computationService.calculateDistance(address1, address2);
        
        assertNotNull("Distance should not be null", distance);
        assertTrue("Distance should be positive", distance > 0);
        assertTrue("Different cities should have larger distance", distance >= 100);
    }

    @Test
    public void testCalculateDistance_DifferentCountries() {
        System.out.println("Test 3: Calculate distance between different countries");
        Double distance = computationService.calculateDistance(address1, address3);
        
        assertNotNull("Distance should not be null", distance);
        assertTrue("Distance should be positive", distance > 0);
        assertTrue("Different countries should have large distance", distance >= 200);
    }

    @Test
    public void testCalculateShipmentCost_Basic() {
        System.out.println("Test 4: Calculate basic shipment cost");
        Double cost = computationService.calculateShipmentCost(testShipment);
        
        assertNotNull("Cost should not be null", cost);
        assertTrue("Cost should be positive", cost > 0);
        System.out.println("  Calculated cost: " + cost);
    }

    @Test
    public void testCalculateShipmentCost_WithSpecialHandling() {
        System.out.println("Test 5: Calculate cost with special handling");
        
        // Calculate cost without special handling
        Double costWithoutSpecial = computationService.calculateShipmentCost(testShipment);
        
        // Add special handling
        testShipment.setSpecialHandlingRequirements("Fragile - Handle with care");
        Double costWithSpecial = computationService.calculateShipmentCost(testShipment);
        
        assertNotNull("Cost with special handling should not be null", costWithSpecial);
        assertTrue("Special handling should increase cost", costWithSpecial > costWithoutSpecial);
        
        double surchargePercentage = ((costWithSpecial - costWithoutSpecial) / costWithoutSpecial) * 100;
        System.out.println("  Surcharge percentage: " + String.format("%.1f%%", surchargePercentage));
        assertTrue("Surcharge should be approximately 20%", Math.abs(surchargePercentage - 20.0) < 1.0);
    }

    @Test
    public void testCalculateShipmentCost_HeavyLoad() {
        System.out.println("Test 6: Calculate cost for heavy load");
        testShipment.setWeightKg(2000.0); // Heavy load
        
        Double cost = computationService.calculateShipmentCost(testShipment);
        
        assertNotNull("Cost should not be null", cost);
        assertTrue("Heavy load should have higher cost", cost > 1000);
        System.out.println("  Heavy load cost: " + cost);
    }

    @Test
    public void testCalculateEstimatedDeliveryTime() {
        System.out.println("Test 7: Calculate estimated delivery time");
        Double hours = computationService.calculateEstimatedDeliveryTime(testShipment);
        
        assertNotNull("Delivery time should not be null", hours);
        assertTrue("Delivery time should be positive", hours > 0);
        assertTrue("Delivery time should be reasonable", hours < 100);
        System.out.println("  Estimated delivery time: " + String.format("%.2f hours", hours));
    }

    @Test
    public void testCalculateCapacityUtilization_Equal() {
        System.out.println("Test 8: Calculate capacity utilization - 50% usage");
        Integer utilization = computationService.calculateCapacityUtilization(
            2500.0, 5000.0, 15.0, 30.0
        );
        
        assertNotNull("Utilization should not be null", utilization);
        assertEquals("Utilization should be 50%", Integer.valueOf(50), utilization);
        System.out.println("  Capacity utilization: " + utilization + "%");
    }

    @Test
    public void testCalculateCapacityUtilization_WeightDominant() {
        System.out.println("Test 9: Calculate capacity utilization - weight dominant");
        Integer utilization = computationService.calculateCapacityUtilization(
            4000.0, 5000.0, 10.0, 30.0  // 80% weight, 33% volume
        );
        
        assertNotNull("Utilization should not be null", utilization);
        assertTrue("Utilization should be based on weight (around 80%)", utilization >= 80);
        System.out.println("  Weight-dominant utilization: " + utilization + "%");
    }

    @Test
    public void testCalculateCapacityUtilization_VolumeDominant() {
        System.out.println("Test 10: Calculate capacity utilization - volume dominant");
        Integer utilization = computationService.calculateCapacityUtilization(
            1000.0, 5000.0, 27.0, 30.0  // 20% weight, 90% volume
        );
        
        assertNotNull("Utilization should not be null", utilization);
        assertTrue("Utilization should be based on volume (around 90%)", utilization >= 90);
        System.out.println("  Volume-dominant utilization: " + utilization + "%");
    }

    @Test
    public void testCalculateCapacityUtilization_Full() {
        System.out.println("Test 11: Calculate capacity utilization - 100% full");
        Integer utilization = computationService.calculateCapacityUtilization(
            5000.0, 5000.0, 30.0, 30.0
        );
        
        assertNotNull("Utilization should not be null", utilization);
        assertEquals("Full capacity should be 100%", Integer.valueOf(100), utilization);
        System.out.println("  Full capacity utilization: " + utilization + "%");
    }

    @Test
    public void testCalculateCapacityUtilization_Empty() {
        System.out.println("Test 12: Calculate capacity utilization - empty");
        Integer utilization = computationService.calculateCapacityUtilization(
            0.0, 5000.0, 0.0, 30.0
        );
        
        assertNotNull("Utilization should not be null", utilization);
        assertEquals("Empty should be 0%", Integer.valueOf(0), utilization);
        System.out.println("  Empty capacity utilization: " + utilization + "%");
    }
}
