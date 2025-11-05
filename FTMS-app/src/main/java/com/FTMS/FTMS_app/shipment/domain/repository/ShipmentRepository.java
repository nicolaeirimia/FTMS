package com.FTMS.FTMS_app.shipment.domain.repository;

import com.FTMS.FTMS_app.shipment.domain.model.Shipment;
import com.FTMS.FTMS_app.shipment.domain.model.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByReferenceNumber(String referenceNumber);

    List<Shipment> findByStatus(ShipmentStatus status);

    List<Shipment> findByCustomerId(Long customerId);

    // Folosit pentru a vedea dacă un șofer este deja într-o cursă activă
    Optional<Shipment> findByAssignedDriverIdAndStatusIn(Long driverId, List<ShipmentStatus> statuses);

    // Folosit pentru a vedea dacă un vehicul este deja într-o cursă activă
    Optional<Shipment> findByAssignedVehicleIdAndStatusIn(Long vehicleId, List<ShipmentStatus> statuses);
}