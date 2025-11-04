package com.ftms.service.repository;

import com.ftms.domain.fleet.Driver;
import com.ftms.domain.common.DriverStatus;
import java.util.Collection;

public interface IDriverRepository extends IBaseRepository<Driver, Integer> {
    Collection<Driver> findByStatus(DriverStatus status);
    Collection<Driver> findAvailableDrivers();
    Driver findByLicenseNumber(String licenseNumber);
}
