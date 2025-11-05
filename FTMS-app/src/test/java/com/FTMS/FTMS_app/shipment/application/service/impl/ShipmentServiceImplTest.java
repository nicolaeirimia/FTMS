package com.FTMS.FTMS_app.shipment.application.service.impl;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.FTMS.FTMS_app.customer.application.service.CustomerService;
import com.FTMS.FTMS_app.fleet.application.service.FleetService;
import com.FTMS.FTMS_app.fleet.domain.model.Driver;
import com.FTMS.FTMS_app.fleet.domain.model.Vehicle;
import com.FTMS.FTMS_app.fleet.domain.model.VehicleCapacity;
import com.FTMS.FTMS_app.shipment.domain.model.CargoDetails;
import com.FTMS.FTMS_app.shipment.domain.model.Shipment;
import com.FTMS.FTMS_app.shipment.domain.model.ShipmentStatus;
import com.FTMS.FTMS_app.shipment.domain.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ShipmentServiceImplTest {

    // 1. Machete pentru toate dependențele
    @Mock
    private ShipmentRepository shipmentRepository;
    @Mock
    private FleetService fleetService;
    @Mock
    private CustomerService customerService; // Deși nu e folosit în 'assign', e bine să fie aici

    // 2. Machete pentru entitățile de domeniu
    // Avem nevoie de ele pentru a le controla metodele (ex: isAvailable)
    @Mock
    private Shipment shipment;
    @Mock
    private Driver driver;
    @Mock
    private Vehicle vehicle;
    @Mock
    private VehicleCapacity vehicleCapacity;
    @Mock
    private CargoDetails cargoDetails;

    // 3. Injectează machetele în serviciul țintă
    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    // Metodă de setup rulată înainte de fiecare test
    @BeforeEach
    void setUp() {
        // Configurăm comportamentul de bază al machetelor

        // Când serviciul cere entitățile, returnează machetele noastre
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(fleetService.getDriverById(1L)).thenReturn(driver);
        when(fleetService.getVehicleById(1L)).thenReturn(vehicle);

        // Configurăm entitățile pentru a trece validările
        when(shipment.canBeAssigned()).thenReturn(true);
        when(shipment.getCargoDetails()).thenReturn(cargoDetails);
        when(driver.isAvailable()).thenReturn(true);
        when(vehicle.isAvailable()).thenReturn(true);
        when(driver.canDriveVehicle(vehicle)).thenReturn(true);

        // Configurăm capacitatea
        when(vehicle.getCapacity()).thenReturn(vehicleCapacity);
        when(vehicleCapacity.isSufficient(anyDouble(), anyDouble())).thenReturn(true);
    }

    /**
     * Testează Workflow-ul de alocare (cazul de succes)
     */
    @Test
    void testAssignShipment_Success() {
        // ARRANGE (Majoritatea e în setUp)

        // Când se salvează, returnăm tot macheta de shipment
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        // ACT
        Shipment result = shipmentService.assignShipment(1L, 1L, 1L);

        // ASSERT
        assertNotNull(result);

        // VERIFY (Verificăm că toate interacțiunile s-au întâmplat)

        // 1. S-au verificat toate entitățile?
        verify(shipmentRepository).findById(1L);
        verify(fleetService).getDriverById(1L);
        verify(fleetService).getVehicleById(1L);

        // 2. S-au rulat toate validările?
        verify(driver).isAvailable();
        verify(vehicle).isAvailable();
        verify(driver).canDriveVehicle(vehicle);
        verify(vehicleCapacity).isSufficient(anyDouble(), anyDouble());

        // 3. S-a apelat logica de domeniu pentru alocare?
        verify(shipment).assign(1L, 1L);

        // 4. S-au apelat celelalte servicii pentru a schimba starea? (Partea de WORKFLOW)
        verify(fleetService).assignDriver(1L);
        verify(fleetService).assignVehicle(1L);

        // 5. S-a salvat în baza de date la final?
        verify(shipmentRepository).save(shipment);
    }

    /**
     * Testează logica de VALIDATION (Ex: șoferul nu e disponibil)
     */
    @Test
    void testAssignShipment_Failure_DriverNotAvailable() {
        // ARRANGE
        // Suprascriem o singură regulă din setup
        when(driver.isAvailable()).thenReturn(false);

        // ACT & ASSERT
        // Verificăm că aruncă excepția corectă
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            shipmentService.assignShipment(1L, 1L, 1L);
        });

        assertTrue(exception.getMessage().contains("is not available"));

        // VERIFY
        // Verificăm că procesul s-a oprit înainte de a face schimbări
        verify(shipmentRepository, never()).save(any(Shipment.class));
        verify(fleetService, never()).assignDriver(anyLong());
    }

    /**
     * Testează logica de COMPUTATION/VALIDATION (Ex: capacitatea nu e suficientă)
     */
    @Test
    void testAssignShipment_Failure_CapacityInsufficient() {
        // ARRANGE
        // Suprascriem regula de capacitate
        when(vehicleCapacity.isSufficient(anyDouble(), anyDouble())).thenReturn(false);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.assignShipment(1L, 1L, 1L);
        });

        assertTrue(exception.getMessage().contains("capacity is not sufficient"));

        // VERIFY
        // Verificăm că nu s-a salvat sau alocat nimic
        verify(shipmentRepository, never()).save(any(Shipment.class));
        verify(fleetService, never()).assignDriver(anyLong());
        verify(fleetService, never()).assignVehicle(anyLong());
    }
}