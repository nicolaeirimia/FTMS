package com.FTMS.FTMS_app.shipment.presentation;

import com.FTMS.FTMS_app.shipment.application.dto.CreateShipmentRequest;
import com.FTMS.FTMS_app.shipment.application.dto.DeliveryConfirmationDto;
import com.FTMS.FTMS_app.shipment.application.service.ShipmentService;
import com.FTMS.FTMS_app.shipment.domain.model.Shipment;
import com.FTMS.FTMS_app.shipment.domain.model.ShipmentStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<Shipment> createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        Shipment newShipment = shipmentService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newShipment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentById(id));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Shipment> assignShipment(
            @PathVariable Long id,
            @RequestParam Long driverId,
            @RequestParam Long vehicleId) {
        // Exemplu apel: PUT /api/v1/shipments/1/assign?driverId=5&vehicleId=10
        Shipment assignedShipment = shipmentService.assignShipment(id, driverId, vehicleId);
        return ResponseEntity.ok(assignedShipment);
    }

    // --- MODIFICAT AICI: Returnăm Shipment în loc de Void ---
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Shipment> cancelShipment(@PathVariable Long id) {
        Shipment canceledShipment = shipmentService.cancelShipment(id);
        return ResponseEntity.ok(canceledShipment);
    }
    // --------------------------------------------------------

    @PutMapping("/{id}/status/{newStatus}")
    public ResponseEntity<Shipment> updateShipmentStatus(
            @PathVariable Long id,
            @PathVariable ShipmentStatus newStatus) {

        // Validăm că nu se încearcă setarea directă a statusurilor finale prin acest endpoint
        if (newStatus == ShipmentStatus.PICKED_UP || newStatus == ShipmentStatus.IN_TRANSIT) {
            Shipment updatedShipment = shipmentService.updateShipmentStatus(id, newStatus);
            return ResponseEntity.ok(updatedShipment);
        } else {
            // Returnăm 400 Bad Request dacă încearcă să seteze DELIVERED manual
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/confirm-delivery")
    public ResponseEntity<Shipment> confirmDelivery(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryConfirmationDto dto) {
        Shipment deliveredShipment = shipmentService.confirmDelivery(id, dto);
        return ResponseEntity.ok(deliveredShipment);
    }
}