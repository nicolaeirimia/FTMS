package com.FTMS.FTMS_app.fleet.domain.repository;

import com.FTMS.FTMS_app.fleet.domain.model.Vehicle;
import com.FTMS.FTMS_app.fleet.domain.model.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Găsește un vehicul după numărul de înmatriculare (pentru validare unicitate).
     */
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    /**
     * Găsește toate vehiculele disponibile care îndeplinesc cerințele de capacitate.
     * Aceasta este metoda cheie pentru alocarea curselor.
     */
    @Query("SELECT v FROM Vehicle v " +
            "WHERE v.status = :status " +
            "AND v.capacity.maxWeightKg >= :weight " +
            "AND v.capacity.maxVolumeCubicMeters >= :volume")
    List<Vehicle> findAvailableVehiclesByCapacity(VehicleStatus status, double weight, double volume);

    /**
     * Găsește toate vehiculele cu un anumit status.
     */
    List<Vehicle> findByStatus(VehicleStatus status);
}