package com.FTMS.FTMS_app.shipment.application.service;

import com.FTMS.FTMS_app.shipment.application.dto.CreateShipmentRequest;
import com.FTMS.FTMS_app.shipment.application.dto.DeliveryConfirmationDto;
import com.FTMS.FTMS_app.shipment.domain.model.Shipment;
import com.FTMS.FTMS_app.shipment.domain.model.ShipmentStatus;

public interface ShipmentService {


    Shipment createShipment(CreateShipmentRequest request);


    Shipment assignShipment(Long shipmentId, Long driverId, Long vehicleId);


    Shipment cancelShipment(Long shipmentId);


    Shipment updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus);


    Shipment confirmDelivery(Long shipmentId, DeliveryConfirmationDto dto);


    Shipment getShipmentById(Long id);
}