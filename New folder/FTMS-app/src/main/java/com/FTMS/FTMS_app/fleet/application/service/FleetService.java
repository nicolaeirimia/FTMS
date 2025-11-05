package com.FTMS.FTMS_app.fleet.application.service;

import com.FTMS.FTMS_app.fleet.application.dto.CreateDriverRequest;
import com.FTMS.FTMS_app.fleet.application.dto.CreateVehicleRequest;
import com.FTMS.FTMS_app.fleet.application.dto.MaintenanceRecordDto;
import com.FTMS.FTMS_app.fleet.domain.model.Driver;
import com.FTMS.FTMS_app.fleet.domain.model.Vehicle;

import java.util.List;

public interface FleetService {

    /**
     * Use Case: Adăugarea unui vehicul nou
     */
    Vehicle addVehicle(CreateVehicleRequest request);

    /**
     * Use Case: Adăugarea unui șofer nou
     */
    Driver addDriver(CreateDriverRequest request);

    /**
     * Use Case: Programarea mentenanței
     */
    void scheduleMaintenance(Long vehicleId);

    /**
     * Use Case: Finalizarea mentenanței
     */
    void completeMaintenance(Long vehicleId, MaintenanceRecordDto recordDto);

    /**
     * Use Case: Alocarea unui vehicul principal unui șofer
     */
    void assignPrimaryVehicle(Long driverId, Long vehicleId);

    /**
     * Găsește un vehicul după ID
     */
    Vehicle getVehicleById(Long id);

    /**
     * Găsește un șofer după ID
     */
    Driver getDriverById(Long id);

    /**
     * Găsește toți șoferii disponibili
     */
    List<Driver> findAvailableDrivers();

    /**
     * Găsește toate vehiculele disponibile
     */
    List<Vehicle> findAvailableVehicles();

    // ... (metodele existente ca addVehicle, addDriver, etc.)

    /**
     * Alocă un șofer unei curse.
     */
    void assignDriver(Long driverId);

    /**
     * Alocă un vehicul unei curse.
     */
    void assignVehicle(Long vehicleId);

    /**
     * Eliberează un șofer după o cursă.
     */
    void releaseDriver(Long driverId);

    /**
     * Eliberează un vehicul după o cursă.
     */
    void releaseVehicle(Long vehicleId);
}