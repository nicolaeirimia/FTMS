package com.ftms.service.workflow;

import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.fleet.Vehicle;
import com.ftms.domain.fleet.Driver;
import com.ftms.domain.shipment.DeliveryConfirmation;

/**
 * Workflow Service Interface
 * Manages business workflows for shipment lifecycle
 */
public interface IWorkflowService {
    Shipment createShipment(Shipment shipment) throws Exception;
    Shipment assignResources(Shipment shipment, Vehicle vehicle, Driver driver) throws Exception;
    Shipment pickupShipment(Shipment shipment) throws Exception;
    Shipment startTransit(Shipment shipment) throws Exception;
    Shipment completeDelivery(Shipment shipment, DeliveryConfirmation confirmation) throws Exception;
    Shipment cancelShipment(Shipment shipment) throws Exception;
}
