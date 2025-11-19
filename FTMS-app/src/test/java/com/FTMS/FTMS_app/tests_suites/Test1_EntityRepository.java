package com.FTMS.FTMS_app.tests_suites;

import com.FTMS.FTMS_app.fleet.domain.model.*;
import com.FTMS.FTMS_app.fleet.domain.repository.VehicleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Test1_EntityRepository {

    @Autowired private VehicleRepository vehicleRepository;

    @Test
    @Order(1)
    @Transactional
    //@Commit
    public void test1_CRUD_Operations() {
        System.out.println("Test 1.1: CRUD Complet (Create, Read, Update, Delete)");
        long uniqueId = System.currentTimeMillis();

        // 1. CREATE
        Vehicle v = new Vehicle();
        v.setRegistrationNumber("B-" + (uniqueId % 1000));
        v.setStatus(VehicleStatus.AVAILABLE);
        v.setVehicleType(VehicleType.FLATBED);
        v.setCapacity(new VehicleCapacity(10000, 50));
        v.setInsuranceExpiryDate(LocalDate.now().plusYears(1));
        v.setRegistrationExpiryDate(LocalDate.now().plusYears(1));
        v = vehicleRepository.save(v);

        assertNotNull(v.getId());

        // 2. READ (Find by Custom Field)
        Optional<Vehicle> found = vehicleRepository.findByRegistrationNumber(v.getRegistrationNumber());
        assertTrue(found.isPresent());
        assertEquals(VehicleType.FLATBED, found.get().getVehicleType());

        // 3. UPDATE
        v.setCurrentMileage(5000);
        vehicleRepository.save(v);
        assertEquals(5000, vehicleRepository.findById(v.getId()).get().getCurrentMileage());

        // 4. DELETE
        vehicleRepository.delete(v);
        assertFalse(vehicleRepository.findById(v.getId()).isPresent());
    }

    @Test
    @Order(2)
    @Transactional
    public void test2_DuplicateConstraint() {
        System.out.println("Test 1.2: Verificare Unicitate (Trebuie să crape)");

        // Încercăm să salvăm două vehicule cu același număr de înmatriculare
        String duplicatePlate = "IF-DUPLICATE";

        Vehicle v1 = new Vehicle();
        v1.setRegistrationNumber(duplicatePlate);
        v1.setStatus(VehicleStatus.AVAILABLE);
        v1.setVehicleType(VehicleType.BOX_TRUCK); // Setăm tipul (obligatoriu)
        v1.setCapacity(new VehicleCapacity(100, 10)); // Setăm capacitatea
        v1.setInsuranceExpiryDate(LocalDate.now());
        v1.setRegistrationExpiryDate(LocalDate.now());
        vehicleRepository.save(v1);

        Vehicle v2 = new Vehicle();
        v2.setRegistrationNumber(duplicatePlate); // Același număr!
        v2.setStatus(VehicleStatus.AVAILABLE);
        v2.setVehicleType(VehicleType.BOX_TRUCK);
        v2.setCapacity(new VehicleCapacity(100, 10));
        v2.setInsuranceExpiryDate(LocalDate.now());
        v2.setRegistrationExpiryDate(LocalDate.now());

        // Ne așteptăm să primim o eroare de la baza de date
        assertThrows(DataIntegrityViolationException.class, () -> {
            vehicleRepository.save(v2);
            vehicleRepository.flush(); // Forțăm scrierea în DB
        });
    }
}