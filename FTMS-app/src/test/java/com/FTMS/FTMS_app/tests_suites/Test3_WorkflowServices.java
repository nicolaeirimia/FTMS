package com.FTMS.FTMS_app.tests_suites;

import com.FTMS.FTMS_app.customer.domain.model.Customer;
import com.FTMS.FTMS_app.customer.domain.model.CustomerStatus;
import com.FTMS.FTMS_app.customer.domain.repository.CustomerRepository;
import com.FTMS.FTMS_app.fleet.domain.model.*;
import com.FTMS.FTMS_app.fleet.domain.repository.DriverRepository;
import com.FTMS.FTMS_app.fleet.domain.repository.VehicleRepository;
import com.FTMS.FTMS_app.shipment.application.dto.*;
import com.FTMS.FTMS_app.shipment.application.service.ShipmentService;
import com.FTMS.FTMS_app.shipment.domain.model.Shipment;
import com.FTMS.FTMS_app.shipment.domain.model.ShipmentStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Test3_WorkflowServices {

    @Autowired private ShipmentService shipmentService;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private DriverRepository driverRepository;
    @Autowired private VehicleRepository vehicleRepository;

    // Helper pentru date comune
    private Customer createCustomer(long id) {
        return customerRepository.save(Customer.builder()
                .companyName("Client " + id).taxIdNumber("RO" + id)
                .primaryContactName("Ion").primaryContactPhone("07").primaryContactEmail("c"+id+"@t.com")
                .status(CustomerStatus.ACTIVE).build());
    }

    @Test
    @Transactional
    //@Commit
    @Order(1)
    public void test1_Workflow_Success() {
        System.out.println("Test 3.1: Workflow SUCCES");
        long id = System.currentTimeMillis();

        Customer c = createCustomer(id);
        Vehicle v = vehicleRepository.save(Vehicle.builder().registrationNumber("B-"+(id%1000)).status(VehicleStatus.AVAILABLE).vehicleType(VehicleType.BOX_TRUCK).capacity(new VehicleCapacity(20000, 100)).insuranceExpiryDate(LocalDate.now().plusYears(1)).registrationExpiryDate(LocalDate.now().plusYears(1)).build());
        Driver d = driverRepository.save(Driver.builder().name("Sofer "+id).status(DriverStatus.AVAILABLE).licenseInfo(new LicenseInfo("L"+id, LicenseType.CE, LocalDate.now(), LocalDate.now().plusYears(1))).contactDetails(new ContactInfo("07", "e", "a")).emergencyContact(new ContactInfo("07", "e", "a")).build());

        CreateShipmentRequest req = new CreateShipmentRequest();
        req.setReferenceNumber("REF-" + id);
        req.setCustomerId(c.getId());
        req.setPickupDateTime(LocalDateTime.now().plusDays(1));
        req.setRequestedDeliveryDateTime(LocalDateTime.now().plusDays(2));
        req.setPrice(100);
        req.setPickupLocation(new ShipmentLocationDto("A", "B", "1", "C", "D", "07"));
        req.setDeliveryLocation(new ShipmentLocationDto("A", "B", "1", "C", "D", "07"));
        req.setCargoDetails(new CargoDto("Marfa", 1000, 10, null, null));

        Shipment s = shipmentService.createShipment(req);
        Shipment assigned = shipmentService.assignShipment(s.getId(), d.getId(), v.getId());

        assertEquals(ShipmentStatus.SCHEDULED, assigned.getStatus());
    }

    @Test
    @Transactional
    @Order(2)
    public void test2_Workflow_Fail_DriverBusy() {
        System.out.println("Test 3.2: Workflow EȘEC - Șofer ocupat");
        long id = System.currentTimeMillis();

        // 1. Creăm un șofer OCUPAT
        Driver busyDriver = driverRepository.save(Driver.builder()
                .name("Sofer Ocupat").status(DriverStatus.ON_ROUTE) // <--- OCUPAT
                .licenseInfo(new LicenseInfo("L"+id, LicenseType.CE, LocalDate.now(), LocalDate.now().plusYears(1)))
                .contactDetails(new ContactInfo("07", "e", "a")).emergencyContact(new ContactInfo("07", "e", "a")).build());

        Vehicle v = vehicleRepository.save(Vehicle.builder().registrationNumber("B-"+(id%1000)).status(VehicleStatus.AVAILABLE).vehicleType(VehicleType.BOX_TRUCK).capacity(new VehicleCapacity(20000, 100)).insuranceExpiryDate(LocalDate.now().plusYears(1)).registrationExpiryDate(LocalDate.now().plusYears(1)).build());
        Customer c = createCustomer(id);

        // 2. Creăm cursa
        CreateShipmentRequest req = new CreateShipmentRequest();
        req.setReferenceNumber("REF-FAIL-" + id);
        req.setCustomerId(c.getId());
        req.setPickupDateTime(LocalDateTime.now().plusDays(1));
        req.setRequestedDeliveryDateTime(LocalDateTime.now().plusDays(2));
        req.setPrice(100);
        req.setPickupLocation(new ShipmentLocationDto("A", "B", "1", "C", "D", "07"));
        req.setDeliveryLocation(new ShipmentLocationDto("A", "B", "1", "C", "D", "07"));
        req.setCargoDetails(new CargoDto("Marfa", 100, 10, null, null));

        Shipment s = shipmentService.createShipment(req);

        // 3. Încercăm alocarea -> Așteptăm Excepție
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            shipmentService.assignShipment(s.getId(), busyDriver.getId(), v.getId());
        });

        System.out.println("Eroare prinsă corect: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("not available"));
    }

    @Test
    @Transactional
    @Order(3)
    public void test3_Workflow_Fail_VehicleCapacity() {
        System.out.println("Test 3.3: Workflow EȘEC - Capacitate Insuficientă");
        long id = System.currentTimeMillis();

        // 1. Creăm un vehicul MIC (1 tonă)
        Vehicle smallVehicle = vehicleRepository.save(Vehicle.builder().registrationNumber("B-"+(id%1000)).status(VehicleStatus.AVAILABLE).vehicleType(VehicleType.BOX_TRUCK)
                .capacity(new VehicleCapacity(1000, 5)) // <--- MIC
                .insuranceExpiryDate(LocalDate.now().plusYears(1)).registrationExpiryDate(LocalDate.now().plusYears(1)).build());

        Driver d = driverRepository.save(Driver.builder().name("Sofer "+id).status(DriverStatus.AVAILABLE).licenseInfo(new LicenseInfo("L"+id, LicenseType.CE, LocalDate.now(), LocalDate.now().plusYears(1))).contactDetails(new ContactInfo("07", "e", "a")).emergencyContact(new ContactInfo("07", "e", "a")).build());
        Customer c = createCustomer(id);

        // 2. Creăm cursa cu marfă GREA (5 tone)
        CreateShipmentRequest req = new CreateShipmentRequest();
        req.setReferenceNumber("REF-CAP-" + id);
        req.setCustomerId(c.getId());
        req.setPickupDateTime(LocalDateTime.now().plusDays(1));
        req.setRequestedDeliveryDateTime(LocalDateTime.now().plusDays(2));
        req.setPrice(100);
        req.setPickupLocation(new ShipmentLocationDto("A", "B", "1", "C", "D", "07"));
        req.setDeliveryLocation(new ShipmentLocationDto("A", "B", "1", "C", "D", "07"));

        req.setCargoDetails(new CargoDto("Marfa Grea", 5000, 50, null, null)); // 5000 kg > 1000 kg

        Shipment s = shipmentService.createShipment(req);

        // 3. Încercăm alocarea -> Așteptăm Excepție
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.assignShipment(s.getId(), d.getId(), smallVehicle.getId());
        });

        System.out.println("Eroare prinsă corect: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("capacity is not sufficient"));
    }
}