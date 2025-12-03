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

@RestController // Spune Spring-ului că această clasă gestionează cereri HTTP
@RequestMapping("/api/v1/fleet") // Prefixul comun pentru toate rutele
public class FleetController {

    private final FleetService fleetService;

    public FleetController(FleetService fleetService) {
        this.fleetService = fleetService;
    }

    // --- VEHICLES ---

    @PostMapping("/vehicles")
    public ResponseEntity<Vehicle> addVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        // @Valid -> Activează validările din DTO (@NotEmpty, @Min etc.)
        Vehicle newVehicle = fleetService.addVehicle(request);
        return new ResponseEntity<>(newVehicle, HttpStatus.CREATED);
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(fleetService.getVehicleById(id));
    }

    @GetMapping("/vehicles/available")
    public ResponseEntity<List<Vehicle>> getAvailableVehicles() {
        return ResponseEntity.ok(fleetService.findAvailableVehicles());
    }

    @PostMapping("/vehicles/{id}/maintenance")
    public ResponseEntity<Void> completeMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceRecordDto recordDto) {
        fleetService.completeMaintenance(id, recordDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/vehicles/{id}/schedule-maintenance")
    public ResponseEntity<Void> scheduleMaintenance(@PathVariable Long id) {
        fleetService.scheduleMaintenance(id);
        return ResponseEntity.ok().build();
    }

    // --- DRIVERS ---

    @PostMapping("/drivers")
    public ResponseEntity<Driver> addDriver(@Valid @RequestBody CreateDriverRequest request) {
        Driver newDriver = fleetService.addDriver(request);
        return new ResponseEntity<>(newDriver, HttpStatus.CREATED);
    }

    @GetMapping("/drivers/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(fleetService.getDriverById(id));
    }

    @GetMapping("/drivers/available")
    public ResponseEntity<List<Driver>> getAvailableDrivers() {
        return ResponseEntity.ok(fleetService.findAvailableDrivers());
    }

    @PutMapping("/drivers/{driverId}/assign-vehicle/{vehicleId}")
    public ResponseEntity<Void> assignPrimaryVehicle(
            @PathVariable Long driverId,
            @PathVariable Long vehicleId) {
        fleetService.assignPrimaryVehicle(driverId, vehicleId);
        return ResponseEntity.ok().build();
    }
}