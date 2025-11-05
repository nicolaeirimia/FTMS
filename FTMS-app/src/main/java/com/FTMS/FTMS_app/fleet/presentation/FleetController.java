package com.FTMS.FTMS_app.fleet.presentation;

import com.FTMS.FTMS_app.fleet.application.dto.CreateDriverRequest;
import com.FTMS.FTMS_app.fleet.application.dto.CreateVehicleRequest;
import com.FTMS.FTMS_app.fleet.application.dto.MaintenanceRecordDto;
import com.FTMS.FTMS_app.fleet.application.service.FleetService;
import com.FTMS.FTMS_app.fleet.domain.model.Driver;
import com.FTMS.FTMS_app.fleet.domain.model.Vehicle;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marchează clasa ca un controler REST
@RequestMapping("/api/v1/fleet") // Toate rutele din acest controler vor începe cu /api/v1/fleet
public class FleetController {

    private final FleetService fleetService;

    // Injectăm serviciul
    public FleetController(FleetService fleetService) {
        this.fleetService = fleetService;
    }

    // --- Vehicule ---

    @PostMapping("/vehicles") // POST /api/v1/fleet/vehicles
    public ResponseEntity<Vehicle> addVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        // @Valid -> Activează validările din DTO (ex: @NotEmpty)
        // @RequestBody -> Convertește JSON-ul din cerere în obiectul DTO
        Vehicle newVehicle = fleetService.addVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newVehicle);
    }

    @GetMapping("/vehicles/{id}") // GET /api/v1/fleet/vehicles/1
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        // @PathVariable -> Ia "id" din URL
        return ResponseEntity.ok(fleetService.getVehicleById(id));
    }

    @GetMapping("/vehicles/available") // GET /api/v1/fleet/vehicles/available
    public ResponseEntity<List<Vehicle>> getAvailableVehicles() {
        return ResponseEntity.ok(fleetService.findAvailableVehicles());
    }

    @PutMapping("/vehicles/{id}/maintenance/schedule") // PUT /api/v1/fleet/vehicles/1/maintenance/schedule
    public ResponseEntity<Void> scheduleMaintenance(@PathVariable Long id) {
        fleetService.scheduleMaintenance(id);
        return ResponseEntity.ok().build(); // Răspuns 200 OK fără corp
    }

    @PostMapping("/vehicles/{id}/maintenance/complete") // POST /api/v1/fleet/vehicles/1/maintenance/complete
    public ResponseEntity<Void> completeMaintenance(@PathVariable Long id, @Valid @RequestBody MaintenanceRecordDto recordDto) {
        fleetService.completeMaintenance(id, recordDto);
        return ResponseEntity.ok().build();
    }

    // --- Șoferi ---

    @PostMapping("/drivers") // POST /api/v1/fleet/drivers
    public ResponseEntity<Driver> addDriver(@Valid @RequestBody CreateDriverRequest request) {
        Driver newDriver = fleetService.addDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDriver);
    }

    @GetMapping("/drivers/{id}") // GET /api/v1/fleet/drivers/1
    public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(fleetService.getDriverById(id));
    }

    @GetMapping("/drivers/available") // GET /api/v1/fleet/drivers/available
    public ResponseEntity<List<Driver>> getAvailableDrivers() {
        return ResponseEntity.ok(fleetService.findAvailableDrivers());
    }

    @PutMapping("/drivers/{driverId}/assign-vehicle/{vehicleId}") // PUT /api/v1/fleet/drivers/1/assign-vehicle/1
    public ResponseEntity<Void> assignPrimaryVehicle(@PathVariable Long driverId, @PathVariable Long vehicleId) {
        fleetService.assignPrimaryVehicle(driverId, vehicleId);
        return ResponseEntity.ok().build();
    }
}