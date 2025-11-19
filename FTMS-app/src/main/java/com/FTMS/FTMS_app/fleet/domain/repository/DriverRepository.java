package com.FTMS.FTMS_app.fleet.domain.repository;

import com.FTMS.FTMS_app.fleet.domain.model.Driver;
import com.FTMS.FTMS_app.fleet.domain.model.DriverStatus;
import com.FTMS.FTMS_app.fleet.domain.model.LicenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Import necesar
import org.springframework.data.repository.query.Param; // Import necesar
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    /**
     * Găsește un șofer după numărul de permis.
     * Spring Data JPA "intră" automat în obiectul LicenseInfo.
     */
    Optional<Driver> findByLicenseInfoLicenseNumber(String licenseNumber);

    /**
     * Găsește șoferi după status și tip permis.
     */
    List<Driver> findByStatusAndLicenseInfoLicenseType(DriverStatus status, LicenseType type);

    /**
     * Găsește toți șoferii cu un anumit status.
     */
    List<Driver> findByStatus(DriverStatus status);

    // --- CERINȚA PENTRU SPRINT: Custom Query cu @Query ---

    /**
     * O interogare complexă care filtrează după 3 criterii:
     * 1. Status
     * 2. Tip Licență (din obiect embedded)
     * 3. Vechime (data angajării)
     */
    @Query("SELECT d FROM Driver d " +
            "WHERE d.status = :status " +
            "AND d.licenseInfo.licenseType = :licenseType " +
            "AND d.employmentDate <= :maxEmploymentDate")
    List<Driver> findExperiencedAvailableDrivers(
            @Param("status") DriverStatus status,
            @Param("licenseType") LicenseType licenseType,
            @Param("maxEmploymentDate") LocalDate maxEmploymentDate
    );
}