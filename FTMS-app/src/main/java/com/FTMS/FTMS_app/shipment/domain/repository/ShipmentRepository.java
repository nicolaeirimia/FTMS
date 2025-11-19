package com.FTMS.FTMS_app.shipment.domain.repository;

import com.FTMS.FTMS_app.shipment.domain.model.Shipment;
import com.FTMS.FTMS_app.shipment.domain.model.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Import necesar
import org.springframework.data.repository.query.Param; // Import necesar
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByReferenceNumber(String referenceNumber);

    List<Shipment> findByStatus(ShipmentStatus status);

    List<Shipment> findByCustomerId(Long customerId);

    // Metode derivate complexe (Spring generează SQL-ul automat)
    Optional<Shipment> findByAssignedDriverIdAndStatusIn(Long driverId, List<ShipmentStatus> statuses);

    Optional<Shipment> findByAssignedVehicleIdAndStatusIn(Long vehicleId, List<ShipmentStatus> statuses);

    // --- CERINȚA PENTRU SPRINT: Custom Query cu @Query ---

    /**
     * Găsește cursele cu marfă grea (peste o anumită greutate).
     * Demonstrează navigarea în obiectul embedded (cargoDetails.weightKg).
     */
    @Query("SELECT s FROM Shipment s WHERE s.cargoDetails.weightKg > :minWeight")
    List<Shipment> findHeavyShipments(@Param("minWeight") double minWeight);
}