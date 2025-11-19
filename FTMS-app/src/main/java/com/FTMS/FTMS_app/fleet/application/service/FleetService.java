package com.FTMS.FTMS_app.fleet.application.service;

import com.FTMS.FTMS_app.fleet.application.dto.CreateDriverRequest;
import com.FTMS.FTMS_app.fleet.application.dto.CreateVehicleRequest;
import com.FTMS.FTMS_app.fleet.application.dto.MaintenanceRecordDto;
import com.FTMS.FTMS_app.fleet.domain.model.Driver;
import com.FTMS.FTMS_app.fleet.domain.model.Vehicle;

import java.util.List;

public interface FleetService {


    Vehicle addVehicle(CreateVehicleRequest request);


    Driver addDriver(CreateDriverRequest request);


    void scheduleMaintenance(Long vehicleId);


    void completeMaintenance(Long vehicleId, MaintenanceRecordDto recordDto);


    void assignPrimaryVehicle(Long driverId, Long vehicleId);


    Vehicle getVehicleById(Long id);


    Driver getDriverById(Long id);


    List<Driver> findAvailableDrivers();


    List<Vehicle> findAvailableVehicles();


    void assignDriver(Long driverId);


    void assignVehicle(Long vehicleId);


    void releaseDriver(Long driverId);


    void releaseVehicle(Long vehicleId);
}