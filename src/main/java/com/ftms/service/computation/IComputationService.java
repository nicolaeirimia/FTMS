package com.ftms.service.computation;

import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.common.Address;

/**
 * Computation Service Interface
 * Performs calculations for shipments
 */
public interface IComputationService {
    Double calculateDistance(Address from, Address to);
    Double calculateShipmentCost(Shipment shipment);
    Double calculateEstimatedDeliveryTime(Shipment shipment);
    Integer calculateCapacityUtilization(Double usedWeight, Double maxWeight, 
                                        Double usedVolume, Double maxVolume);
}
