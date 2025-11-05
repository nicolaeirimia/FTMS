package com.FTMS.FTMS_app.fleet.domain.repository;

import com.FTMS.FTMS_app.fleet.domain.model.Driver;
import com.FTMS.FTMS_app.fleet.domain.model.DriverStatus;
import com.FTMS.FTMS_app.fleet.domain.model.LicenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Marchează ca fiind un Bean Spring
public interface DriverRepository extends JpaRepository<Driver, Long> {

    /**
     * Găsește un șofer după numărul de permis (pentru validare unicitate).
     */
    Optional<Driver> findByLicenseInfoLicenseNumber(String licenseNumber);

    /**
     * Găsește toți șoferii disponibili care au un anumit tip de permis.
     * Aceasta va fi metoda cheie pentru alocarea curselor.
     */
    List<Driver> findByStatusAndLicenseInfoLicenseType(DriverStatus status, LicenseType type);

    /**
     * Găsește toți șoferii disponibili.
     */
    List<Driver> findByStatus(DriverStatus status);
}