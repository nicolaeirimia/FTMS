package com.FTMS.FTMS_app.fleet.application.service.impl;

import com.FTMS.FTMS_app.common.exception.ResourceNotFoundException;
import com.FTMS.FTMS_app.fleet.application.dto.CreateDriverRequest;
import com.FTMS.FTMS_app.fleet.application.dto.CreateVehicleRequest;
import com.FTMS.FTMS_app.fleet.application.dto.MaintenanceRecordDto;
import com.FTMS.FTMS_app.fleet.application.service.FleetService;
import com.FTMS.FTMS_app.fleet.domain.model.*;
import com.FTMS.FTMS_app.fleet.domain.repository.DriverRepository;
import com.FTMS.FTMS_app.fleet.domain.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FleetServiceImpl implements FleetService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public FleetServiceImpl(VehicleRepository vehicleRepository, DriverRepository driverRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public Vehicle addVehicle(CreateVehicleRequest request) {

        vehicleRepository.findByRegistrationNumber(request.getRegistrationNumber())
                .ifPresent(v -> {
                    throw new IllegalArgumentException("Vehicle with registration number " + request.getRegistrationNumber() + " already exists.");
                });


        VehicleCapacity capacity = new VehicleCapacity(request.getMaxWeightKg(), request.getMaxVolumeCubicMeters());

        Vehicle vehicle = Vehicle.builder()
                .registrationNumber(request.getRegistrationNumber())
                .make(request.getMake())
                .model(request.getModel())
                .vehicleType(request.getVehicleType())
                .yearOfManufacture(request.getYearOfManufacture())
                .capacity(capacity)
                .fuelType(request.getFuelType())
                .currentMileage(request.getCurrentMileage())
                .insurancePolicyNumber(request.getInsurancePolicyNumber())
                .insuranceExpiryDate(request.getInsuranceExpiryDate())
                .registrationExpiryDate(request.getRegistrationExpiryDate())
                .status(VehicleStatus.AVAILABLE)
                .build();

        return vehicleRepository.save(vehicle);
    }

    @Override
    public Driver addDriver(CreateDriverRequest request) {

        driverRepository.findByLicenseInfoLicenseNumber(request.getLicenseNumber())
                .ifPresent(d -> {
                    throw new IllegalArgumentException("Driver with license number " + request.getLicenseNumber() + " already exists.");
                });


        LicenseInfo license = LicenseInfo.builder()
                .licenseNumber(request.getLicenseNumber())
                .licenseType(request.getLicenseType())
                .issueDate(request.getLicenseIssueDate())
                .expiryDate(request.getLicenseExpiryDate())
                .build();

        ContactInfo contact = ContactInfo.builder()
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .build();

        ContactInfo emergencyContact = ContactInfo.builder()
                .name(request.getEmergencyContactName())
                .phone(request.getEmergencyContactPhone())
                .build();


        Driver driver = Driver.builder()
                .name(request.getName())
                .licenseInfo(license)
                .contactDetails(contact)
                .emergencyContact(emergencyContact)
                .employmentDate(request.getEmploymentDate())
                .status(DriverStatus.AVAILABLE)
                .build();

        return driverRepository.save(driver);
    }

    @Override
    @Transactional
    public void scheduleMaintenance(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        vehicle.scheduleMaintenance();
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void completeMaintenance(Long vehicleId, MaintenanceRecordDto recordDto) {
        Vehicle vehicle = getVehicleById(vehicleId);


        MaintenanceRecord record = MaintenanceRecord.builder()
                .date(recordDto.getDate())
                .maintenanceType(recordDto.getMaintenanceType())
                .description(recordDto.getDescription())
                .cost(recordDto.getCost())
                .serviceProvider(recordDto.getServiceProvider())
                .build();

        vehicle.completeMaintenance(record);

        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void assignPrimaryVehicle(Long driverId, Long vehicleId) {
        Driver driver = getDriverById(driverId);
        Vehicle vehicle = getVehicleById(vehicleId);

        if (!driver.canDriveVehicle(vehicle)) {
            throw new IllegalArgumentException("Driver " + driver.getName() + " does not have the correct license (" +
                    driver.getLicenseInfo().getLicenseType() + ") for vehicle type " + vehicle.getVehicleType());
        }

        driver.setPrimaryVehicle(vehicle);
        driverRepository.save(driver);
    }



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

                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
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



    @Override
    @Transactional
    public void assignDriver(Long driverId) {
        Driver driver = getDriverById(driverId);
        driver.assignToShipment();
        driverRepository.save(driver);
    }

    @Override
    @Transactional
    public void assignVehicle(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        vehicle.assignToShipment();
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void releaseDriver(Long driverId) {
        Driver driver = getDriverById(driverId);
        driver.completeShipment();
        driverRepository.save(driver);
    }

    @Override
    @Transactional
    public void releaseVehicle(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        vehicle.releaseFromShipment();
        vehicleRepository.save(vehicle);
    }
}