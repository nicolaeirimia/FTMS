package com.FTMS.FTMS_app.customer.domain.repository;

import com.FTMS.FTMS_app.customer.domain.model.Customer;
import com.FTMS.FTMS_app.customer.domain.model.CustomerCategory;
import com.FTMS.FTMS_app.customer.domain.model.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // --- 1. Metode Standard (Derived Queries) ---
    // Esențiale pentru validări (unicitate)
    Optional<Customer> findByTaxIdNumber(String taxIdNumber);

    Optional<Customer> findByPrimaryContactEmail(String email);

    Optional<Customer> findByRegistrationNumber(String regNumber);


    // --- 2. CUSTOM QUERIES (Logică de Business) ---

    /**
     * Scenario: "Search Bar" pentru agenții de vânzări.
     * Caută clienți după o parte din nume (case-insensitive), dar doar pe cei activi.
     * * SQL Echivalent: SELECT * FROM customers WHERE lower(company_name) LIKE '%text%' AND status = 'ACTIVE'
     */
    @Query("SELECT c FROM Customer c " +
            "WHERE LOWER(c.companyName) LIKE LOWER(CONCAT('%', :nameFragment, '%')) " +
            "AND c.status = 'ACTIVE'")
    List<Customer> searchActiveCustomersByName(@Param("nameFragment") String nameFragment);


    /**
     * Scenario: Analiză Regională / Marketing.
     * Găsește toți clienții VIP dintr-un anumit oraș.
     * Demonstrează: Navigarea în obiectul @Embedded (billingAddress.city).
     */
    @Query("SELECT c FROM Customer c " +
            "WHERE c.billingAddress.city = :city " +
            "AND c.category = :category")
    List<Customer> findCustomersByCityAndCategory(
            @Param("city") String city,
            @Param("category") CustomerCategory category
    );


    /**
     * Scenario: Risk Management.
     * Găsește clienții "riscanți": Au status SUSPENDED dar au o limită de credit mare.
     * (Poate vrei să le reduci limita automat).
     */
    @Query("SELECT c FROM Customer c " +
            "WHERE c.status = :status " +
            "AND c.creditLimit > :threshold")
    List<Customer> findRiskyCustomers(
            @Param("status") CustomerStatus status,
            @Param("threshold") double creditLimitThreshold
    );
}