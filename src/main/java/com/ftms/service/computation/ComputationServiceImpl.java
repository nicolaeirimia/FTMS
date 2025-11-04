package com.ftms.service.computation;

import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.common.Address;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Computation Service Implementation
 */
@Service
public class ComputationServiceImpl implements IComputationService {
    
    private static Logger logger = Logger.getLogger(ComputationServiceImpl.class.getName());
    
    private static final Double BASE_RATE_PER_KM = 2.5;
    private static final Double RATE_PER_KG = 0.5;
    private static final Double AVERAGE_SPEED_KM_H = 60.0;

    @Override
    public Double calculateDistance(Address from, Address to) {
        // Simplified calculation - in reality would use geocoding API
        // For now, return a random distance for demonstration
        double baseDistance = 100.0;
        
        if (!from.getCity().equals(to.getCity())) {
            baseDistance = 200.0;
        }
        
        if (!from.getCountry().equals(to.getCountry())) {
            baseDistance = 500.0;
        }
        
        logger.info(String.format("Calculated distance from %s to %s: %.2f km", 
                    from.getCity(), to.getCity(), baseDistance));
        return baseDistance;
    }

    @Override
    public Double calculateShipmentCost(Shipment shipment) {
        Double distance = calculateDistance(
            shipment.getPickupLocation().getAddress(),
            shipment.getDeliveryLocation().getAddress()
        );
        
        Double weightCost = shipment.getWeightKg() * RATE_PER_KG;
        Double distanceCost = distance * BASE_RATE_PER_KM;
        Double totalCost = weightCost + distanceCost;
        
        // Add special handling surcharge if needed
        if (shipment.getSpecialHandlingRequirements() != null && 
            !shipment.getSpecialHandlingRequirements().isEmpty()) {
            totalCost *= 1.2; // 20% surcharge
        }
        
        logger.info(String.format("Calculated shipment cost: %.2f", totalCost));
        return totalCost;
    }

    @Override
    public Double calculateEstimatedDeliveryTime(Shipment shipment) {
        Double distance = calculateDistance(
            shipment.getPickupLocation().getAddress(),
            shipment.getDeliveryLocation().getAddress()
        );
        
        Double hours = distance / AVERAGE_SPEED_KM_H;
        
        logger.info(String.format("Estimated delivery time: %.2f hours", hours));
        return hours;
    }

    @Override
    public Integer calculateCapacityUtilization(Double usedWeight, Double maxWeight,
                                               Double usedVolume, Double maxVolume) {
        Double weightUtilization = (usedWeight / maxWeight) * 100;
        Double volumeUtilization = (usedVolume / maxVolume) * 100;
        
        // Return the maximum of the two utilizations
        Integer utilization = (int) Math.max(weightUtilization, volumeUtilization);
        
        logger.info(String.format("Capacity utilization: %d%%", utilization));
        return utilization;
    }
}
