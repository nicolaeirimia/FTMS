package com.ftms.service.repository;

import com.ftms.domain.fleet.Vehicle;
import com.ftms.domain.common.VehicleStatus;
import java.util.Collection;

public interface IVehicleRepository extends IBaseRepository<Vehicle, Integer> {
    Collection<Vehicle> findByStatus(VehicleStatus status);
    Collection<Vehicle> findAvailableVehicles();
    Vehicle findByRegistrationNumber(String registrationNumber);
}
