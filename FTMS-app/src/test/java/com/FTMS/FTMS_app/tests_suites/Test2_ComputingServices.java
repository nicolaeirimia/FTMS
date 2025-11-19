package com.FTMS.FTMS_app.tests_suites;

import com.FTMS.FTMS_app.customer.domain.model.*;
import com.FTMS.FTMS_app.fleet.domain.model.VehicleCapacity;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Test2_ComputingServices {

    @Test
    @Order(1)
    void testInvoiceCalculation() {
        System.out.println("Test 2.1: Calcul Financiar (Factură)");
        Invoice invoice = Invoice.builder().totalAmount(1000.0).status(InvoiceStatus.PENDING).build();

        // Plată insuficientă
        invoice.recordPayment(PaymentDetails.builder().amount(999.9).build());
        assertEquals(InvoiceStatus.PARTIALLY_PAID, invoice.getStatus());

        // Plată exactă (epsilon check intern)
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.recordPayment(PaymentDetails.builder().amount(1000.0).build());
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
    }

    @Test
    @Order(2)
    void testCapacityCalculation() {
        System.out.println("Test 2.2: Calcul Logistic (Capacitate Vehicul)");

        VehicleCapacity truckCapacity = new VehicleCapacity(20000, 100); // 20 tone, 100 m3

        // Cazul 1: Marfa încape
        assertTrue(truckCapacity.isSufficient(15000, 50), "15 tone ar trebui să încapă");

        // Cazul 2: Prea greu
        assertFalse(truckCapacity.isSufficient(20001, 10), "20.001 tone NU ar trebui să încapă");

        // Cazul 3: Prea voluminos
        assertFalse(truckCapacity.isSufficient(1000, 101), "101 m3 NU ar trebui să încapă");
    }

    @Test
    @Order(3)
    void testDiscountCalculation() {
        System.out.println("Test 2.3: Calcul Discount (VIP)");

        double originalPrice = 1000.0;

        // Client STANDARD (0% discount)
        double standardPrice = CustomerCategory.STANDARD.applyDiscount(originalPrice);
        assertEquals(1000.0, standardPrice);

        // Client VIP (20% discount)
        double vipPrice = CustomerCategory.VIP.applyDiscount(originalPrice);
        assertEquals(800.0, vipPrice);
    }
}