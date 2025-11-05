package com.FTMS.FTMS_app.shipment.application.service;

import com.FTMS.FTMS_app.shipment.application.dto.CreateShipmentRequest;
import com.FTMS.FTMS_app.shipment.application.dto.DeliveryConfirmationDto;
import com.FTMS.FTMS_app.shipment.domain.model.Shipment;
import com.FTMS.FTMS_app.shipment.domain.model.ShipmentStatus;

public interface ShipmentService {

    /**
     * Use Case: Crearea unui transport (Cursă).
     */
    Shipment createShipment(CreateShipmentRequest request);

    /**
     * Use Case: Alocarea unui șofer și vehicul.
     */
    Shipment assignShipment(Long shipmentId, Long driverId, Long vehicleId);

    /**
     * Use Case: Anularea unui transport.
     */
    void cancelShipment(Long shipmentId);

    /**
     * Use Case: Actualizarea statusului (ex: PICKED_UP, IN_TRANSIT).
     */
    Shipment updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus);

    /**
     * Use Case: Confirmarea livrării și generarea facturii.
     */
    Shipment confirmDelivery(Long shipmentId, DeliveryConfirmationDto dto);

    /**
     * Metodă utilitară.
     */
    Shipment getShipmentById(Long id);
}