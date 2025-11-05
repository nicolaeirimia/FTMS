package com.FTMS.FTMS_app.fleet.application.service.impl;

import com.FTMS.FTMS_app.fleet.application.dto.CreateVehicleRequest;
import com.FTMS.FTMS_app.fleet.domain.model.Vehicle;
import com.FTMS.FTMS_app.fleet.domain.model.VehicleCapacity;
import com.FTMS.FTMS_app.fleet.domain.model.VehicleStatus;
import com.FTMS.FTMS_app.fleet.domain.model.VehicleType;
import com.FTMS.FTMS_app.fleet.domain.repository.DriverRepository;
import com.FTMS.FTMS_app.fleet.domain.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List; // <-- ADĂUGAT IMPORTUL PENTRU 'List'
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Activează Mockito
class FleetServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private FleetServiceImpl fleetService;

    private CreateVehicleRequest createVehicleRequest;

    @BeforeEach
    void setUp() {
        createVehicleRequest = new CreateVehicleRequest();
        createVehicleRequest.setRegistrationNumber("B-123-XYZ");
        createVehicleRequest.setMake("Mercedes");
        createVehicleRequest.setModel("Actros");
        createVehicleRequest.setVehicleType(VehicleType.BOX_TRUCK);
        createVehicleRequest.setYearOfManufacture(2020);
        createVehicleRequest.setMaxWeightKg(20000);
        createVehicleRequest.setMaxVolumeCubicMeters(90);
        createVehicleRequest.setInsuranceExpiryDate(LocalDate.now().plusYears(1));
        createVehicleRequest.setRegistrationExpiryDate(LocalDate.now().plusYears(1));
    }

    @Test
    void testAddVehicle_Success() {
        // ARRANGE
        when(vehicleRepository.findByRegistrationNumber("B-123-XYZ")).thenReturn(Optional.empty());

        // --- AICI ESTE MODIFICAREA PENTRU 'setId' ---
        // În loc să modificăm 'vehicleToSave', returnăm un obiect nou
        // folosind constructorul (@AllArgsConstructor) pe care Vehicle îl are.
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle vehicleToSave = invocation.getArgument(0);

            // Simulăm salvarea returnând un obiect nou cu ID-ul setat
            return new Vehicle(
                    1L, // ID-ul simulat
                    vehicleToSave.getRegistrationNumber(),
                    vehicleToSave.getMake(),
                    vehicleToSave.getModel(),
                    vehicleToSave.getVehicleType(),
                    vehicleToSave.getYearOfManufacture(),
                    vehicleToSave.getCapacity(),
                    vehicleToSave.getFuelType(),
                    vehicleToSave.getCurrentMileage(),
                    vehicleToSave.getInsurancePolicyNumber(),
                    vehicleToSave.getInsuranceExpiryDate(),
                    vehicleToSave.getRegistrationExpiryDate(),
                    vehicleToSave.getStatus(),
                    vehicleToSave.getMaintenanceHistory()
            );
        });
        // --- SFÂRȘITUL MODIFICĂRII ---

        // ACT
        Vehicle result = fleetService.addVehicle(createVehicleRequest);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId()); // Acum ID-ul este 1L, conform simulării
        assertEquals("B-123-XYZ", result.getRegistrationNumber());
        assertEquals(VehicleStatus.AVAILABLE, result.getStatus());

        verify(vehicleRepository, times(1)).findByRegistrationNumber("B-123-XYZ");
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testAddVehicle_Failure_DuplicateRegistration() {
        // ARRANGE
        // Aici era eroarea cu 'List'. Acum ar trebui să funcționeze
        // datorită importului 'java.util.List'.
        Vehicle existingVehicle = new Vehicle(1L, "B-123-XYZ", "Ford", "Transit",
                VehicleType.BOX_TRUCK, 2019, new VehicleCapacity(1000, 10), "Diesel",
                100000, "policy", LocalDate.now(), LocalDate.now(), VehicleStatus.AVAILABLE, List.of());

        when(vehicleRepository.findByRegistrationNumber("B-123-XYZ")).thenReturn(Optional.of(existingVehicle));

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fleetService.addVehicle(createVehicleRequest);
        });

        assertTrue(exception.getMessage().contains("already exists"));

        // VERIFY
        verify(vehicleRepository, times(1)).findByRegistrationNumber("B-123-XYZ");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
}