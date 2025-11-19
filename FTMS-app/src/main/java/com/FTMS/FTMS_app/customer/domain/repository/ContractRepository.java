package com.FTMS.FTMS_app.customer.domain.repository;

import com.FTMS.FTMS_app.customer.domain.model.Contract;
import com.FTMS.FTMS_app.customer.domain.model.ServiceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    // --- 1. Standard Derived Query (Metodă Derivată) ---
    /**
     * Util pentru job-uri cron: "Găsește toate contractele care expiră luna viitoare".
     */
    List<Contract> findByEndDateBetween(LocalDate startDate, LocalDate endDate);


    // --- 2. CUSTOM QUERIES (Logică de Business Complexă) ---

    /**
     * Scenario: Validare la crearea unei comenzi (Shipment).
     * Vrem să știm dacă clientul are un contract ACTIV chiar în acest moment.
     * * Logica: Contractul aparține clientului X, iar ziua de azi este între data de start și data de final.
     */
    @Query("SELECT c FROM Contract c " +
            "WHERE c.customer.id = :customerId " +
            "AND :referenceDate BETWEEN c.startDate AND c.endDate")
    Optional<Contract> findActiveContractForCustomer(
            @Param("customerId") Long customerId,
            @Param("referenceDate") LocalDate referenceDate
    );


    /**
     * Scenario: Validare la crearea unui CONTRACT NOU.
     * Verifică dacă există deja un contract care se SUPRAPUNE cu noile date propuse.
     * Regula: Un client nu poate avea două contracte active simultan.
     * * Logica de suprapunere (Overlap Logic):
     * (StartA <= EndB) și (EndA >= StartB)
     */
    @Query("SELECT c FROM Contract c " +
            "WHERE c.customer.id = :customerId " +
            "AND c.startDate <= :newEndDate " +
            "AND c.endDate >= :newStartDate")
    List<Contract> findOverlappingContracts(
            @Param("customerId") Long customerId,
            @Param("newStartDate") LocalDate newStartDate,
            @Param("newEndDate") LocalDate newEndDate
    );


    /**
     * Scenario: Rapoarte Manageriale.
     * Găsește toate contractele VIP/EXPRESS active pentru a prioritiza resursele.
     */
    @Query("SELECT c FROM Contract c " +
            "WHERE c.serviceLevel = :serviceLevel " +
            "AND c.endDate >= :today")
    List<Contract> findActiveContractsByServiceLevel(
            @Param("serviceLevel") ServiceLevel serviceLevel,
            @Param("today") LocalDate today
    );
}