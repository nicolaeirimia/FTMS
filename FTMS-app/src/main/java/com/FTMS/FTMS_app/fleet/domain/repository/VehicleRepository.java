package com.FTMS.FTMS_app.fleet.domain.repository;

import com.FTMS.FTMS_app.fleet.domain.model.Vehicle;
import com.FTMS.FTMS_app.fleet.domain.model.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // <-- IMPORT NECESAR
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    @Query("SELECT v FROM Vehicle v " +
            "WHERE v.status = :status " +
            "AND v.capacity.maxWeightKg >= :weight " +
            "AND v.capacity.maxVolumeCubicMeters >= :volume")
    List<Vehicle> findAvailableVehiclesByCapacity(
            @Param("status") VehicleStatus status,
            @Param("weight") double weight,
            @Param("volume") double volume
    );

    List<Vehicle> findByStatus(VehicleStatus status);
}