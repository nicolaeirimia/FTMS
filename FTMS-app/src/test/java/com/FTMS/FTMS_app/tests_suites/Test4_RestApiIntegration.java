package com.FTMS.FTMS_app.tests_suites;

import com.FTMS.FTMS_app.fleet.application.dto.CreateVehicleRequest;
import com.FTMS.FTMS_app.fleet.domain.model.Vehicle;
import com.FTMS.FTMS_app.fleet.domain.model.VehicleType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Commit;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Test4_RestApiIntegration {

    @Autowired
    private TestRestTemplate restTemplate;


    private static Long createdVehicleId;

    @Test
    @Order(1)
    @Commit
    public void test1_CreateVehicle_Success() {
        System.out.println("REST Test 1: Create Vehicle (POST) - Happy Path");

        // 1. Pregătim Request-ul (DTO)
        CreateVehicleRequest request = new CreateVehicleRequest();
        request.setRegistrationNumber("REST-TEST-" + System.currentTimeMillis());
        request.setMake("Mercedes");
        request.setModel("Actros");
        request.setVehicleType(VehicleType.BOX_TRUCK);
        request.setYearOfManufacture(2023);
        request.setMaxWeightKg(20000);
        request.setMaxVolumeCubicMeters(100);
        request.setFuelType("Diesel");
        request.setInsurancePolicyNumber("INS-REST");
        request.setInsuranceExpiryDate(LocalDate.now().plusYears(1));
        request.setRegistrationExpiryDate(LocalDate.now().plusYears(1));

        // 2. Executăm apelul HTTP real
        ResponseEntity<Vehicle> response = restTemplate.postForEntity(
                "/api/v1/fleet/vehicles",
                request,
                Vehicle.class
        );

        // 3. Verificăm Statusul HTTP și Body-ul
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        
        // Salvăm ID-ul pentru testul următor
        createdVehicleId = response.getBody().getId();
        System.out.println("Vehicul creat prin API cu ID: " + createdVehicleId);
    }

    @Test
    @Order(2)
    public void test2_GetVehicle_Success() {
        System.out.println("REST Test 2: Get Vehicle (GET) - Happy Path");
        
        // Verificăm că îl putem citi înapoi
        ResponseEntity<Vehicle> response = restTemplate.getForEntity(
                "/api/v1/fleet/vehicles/" + createdVehicleId,
                Vehicle.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Mercedes", response.getBody().getMake());
    }

    @Test
    @Order(3)
    public void test3_CreateVehicle_ValidationError() {
        System.out.println("REST Test 3: Validation Error (400 Bad Request)");

        // Trimitem un request invalid (fără registrationNumber)
        CreateVehicleRequest invalidRequest = new CreateVehicleRequest();
        invalidRequest.setMake("Invalid Car");
        // Lipsesc câmpuri obligatorii marcate cu @NotEmpty

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/fleet/vehicles",
                invalidRequest,
                String.class
        );

        // API-ul trebuie să respingă cererea
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        System.out.println("Răspuns eroare: " + response.getBody());
    }

    @Test
    @Order(4)
    public void test4_GetVehicle_NotFound() {
        System.out.println("REST Test 4: Resource Not Found (404 Not Found)");

        // Căutăm un ID care sigur nu există
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/fleet/vehicles/999999",
                String.class
        );

        // GlobalExceptionHandler trebuie să prindă ResourceNotFoundException
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}