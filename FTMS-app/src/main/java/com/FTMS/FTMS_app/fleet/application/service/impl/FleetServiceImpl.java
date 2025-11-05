package com.FTMS.FTMS_app.fleet.application.service.impl;

import com.FTMS.FTMS_app.fleet.application.dto.CreateDriverRequest;
import com.FTMS.FTMS_app.fleet.application.dto.CreateVehicleRequest;
import com.FTMS.FTMS_app.fleet.application.dto.MaintenanceRecordDto;
import com.FTMS.FTMS_app.fleet.application.service.FleetService;
import com.FTMS.FTMS_app.fleet.domain.model.*;
import com.FTMS.FTMS_app.common.exception.ResourceNotFoundException;
import com.FTMS.FTMS_app.fleet.domain.repository.DriverRepository;
import com.FTMS.FTMS_app.fleet.domain.repository.VehicleRepository;
// Importă excepția pe care o vom crea la pasul următor
// import com.FTMS.FTMS_app.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional // Toate metodele publice vor rula într-o tranzacție
public class FleetServiceImpl implements FleetService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    // Constructor Injection (recomandat de Spring)
    public FleetServiceImpl(VehicleRepository vehicleRepository, DriverRepository driverRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public Vehicle addVehicle(CreateVehicleRequest request) {
        // 1. Validare (Domain Validation Service - menționat în slide-uri)
        // Verifică unicitatea nr. de înmatriculare
        vehicleRepository.findByRegistrationNumber(request.getRegistrationNumber())
                .ifPresent(v -> {
                    throw new IllegalArgumentException("Vehicle with registration number " + request.getRegistrationNumber() + " already exists.");
                });

        // 2. Mapare DTO -> Model
        VehicleCapacity capacity = new VehicleCapacity(request.getMaxWeightKg(), request.getMaxVolumeCubicMeters());

        Vehicle vehicle = new Vehicle(
                null, // ID-ul va fi generat de JPA
                request.getRegistrationNumber(),
                request.getMake(),
                request.getModel(),
                request.getVehicleType(),
                request.getYearOfManufacture(),
                capacity,
                request.getFuelType(),
                request.getCurrentMileage(),
                request.getInsurancePolicyNumber(),
                request.getInsuranceExpiryDate(),
                request.getRegistrationExpiryDate(),
                VehicleStatus.AVAILABLE, // Status inițial
                List.of() // Fără istoric de mentenanță
        );

        // 3. Salvare (Repository)
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Driver addDriver(CreateDriverRequest request) {
        // 1. Validare
        driverRepository.findByLicenseInfoLicenseNumber(request.getLicenseNumber())
                .ifPresent(d -> {
                    throw new IllegalArgumentException("Driver with license number " + request.getLicenseNumber() + " already exists.");
                });

        // 2. Mapare DTO -> Model (Value Objects)
        LicenseInfo license = new LicenseInfo(
                request.getLicenseNumber(),
                request.getLicenseType(),
                request.getLicenseIssueDate(),
                request.getLicenseExpiryDate()
        );

        ContactInfo contact = new ContactInfo(
                request.getPhone(),
                request.getEmail(),
                request.getAddress()
        );

        ContactInfo emergencyContact = new ContactInfo(
                request.getEmergencyContactName(),
                request.getEmergencyContactPhone(),
                null, // Fără adresă pentru contactul de urgență
                null
        );

        Driver driver = new Driver(
                null, // ID generat
                request.getName(),
                license,
                contact,
                emergencyContact,
                request.getEmploymentDate(),
                DriverStatus.AVAILABLE, // Status inițial
                null // Fără vehicul principal la început
        );

        // 3. Salvare
        return driverRepository.save(driver);
    }

    @Override
    @Transactional // Asigură-te că modificarea este salvată
    public void scheduleMaintenance(Long vehicleId) {
        // 1. Găsește entitatea
        Vehicle vehicle = getVehicleById(vehicleId);
        // 2. Apelează logica de business din domeniu
        vehicle.scheduleMaintenance();
        // 3. Salvează (JPA o face automat la finalul tranzacției, dar putem fi expliciți)
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void completeMaintenance(Long vehicleId, MaintenanceRecordDto recordDto) {
        // 1. Găsește entitatea
        Vehicle vehicle = getVehicleById(vehicleId);

        // 2. Mapare DTO -> Model
        MaintenanceRecord record = new MaintenanceRecord(
                recordDto.getDate(),
                recordDto.getMaintenanceType(),
                recordDto.getDescription(),
                recordDto.getCost(),
                recordDto.getServiceProvider()
        );

        // 3. Apelează logica de business
        vehicle.completeMaintenance(record);

        // 4. Salvează
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void assignPrimaryVehicle(Long driverId, Long vehicleId) {
        Driver driver = getDriverById(driverId);
        Vehicle vehicle = getVehicleById(vehicleId);

        // Aici putem adăuga validări suplimentare, ex:
        if (!driver.canDriveVehicle(vehicle)) {
            throw new IllegalArgumentException("Driver " + driver.getName() + " does not have the correct license (" +
                    driver.getLicenseInfo().getLicenseType() + ") for vehicle type " + vehicle.getVehicleType());
        }

        driver.setPrimaryVehicle(vehicle);
        driverRepository.save(driver);
    }

    // --- Metode Utilitare (Helpers) ---

    @Override
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        // .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Driver> findAvailableDrivers() {
        return driverRepository.findByStatus(DriverStatus.AVAILABLE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);
    }

    // ... (metodele existente în FleetServiceImpl)

    @Override
    @Transactional
    public void assignDriver(Long driverId) {
        Driver driver = getDriverById(driverId);
        // Apelează logica de business din domeniu
        driver.assignToShipment();
        driverRepository.save(driver);
    }

    @Override
    @Transactional
    public void assignVehicle(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        // Apelează logica de business din domeniu
        vehicle.assignToShipment();
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void releaseDriver(Long driverId) {
        Driver driver = getDriverById(driverId);
        driver.completeShipment(); // Metoda de business din domeniu
        driverRepository.save(driver);
    }

    @Override
    @Transactional
    public void releaseVehicle(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        vehicle.releaseFromShipment(); // Metoda de business din domeniu
        vehicleRepository.save(vehicle);
    }
}