package com.FTMS.FTMS_app.customer.domain.repository;

import com.FTMS.FTMS_app.customer.domain.model.Invoice;
import com.FTMS.FTMS_app.customer.domain.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // --- 1. Derived Queries (Standard) ---

    List<Invoice> findByCustomerId(Long customerId);

    List<Invoice> findByStatus(InvoiceStatus status);

    /**
     * Găsește facturile unui client cu un anumit status (ex: toate facturile neplătite).
     */
    List<Invoice> findByCustomerIdAndStatus(Long customerId, InvoiceStatus status);


    // --- 2. CUSTOM QUERIES (Rapoarte Financiare) ---

    /**
     * Calculează totalul datorat de un client (Restanțe).
     * * Îmbunătățiri față de varianta ta:
     * 1. Folosește :status ca parametru (nu string hardcodat).
     * 2. Folosește COALESCE(SUM(...), 0.0) -> Dacă nu găsește nimic, returnează 0.0, nu NULL.
     */
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.0) FROM Invoice i " +
            "WHERE i.customer.id = :customerId " +
            "AND i.status = :status")
    Double calculateTotalAmountByStatus(
            @Param("customerId") Long customerId,
            @Param("status") InvoiceStatus status
    );

    /**
     * Scenario: Raport de Vânzări pe o perioadă (ex: Luna curentă).
     * Calculează venitul total (facturi emise) între două date.
     */
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.0) FROM Invoice i " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate")
    Double calculateTotalRevenueBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Scenario: Dashboard Client.
     * Găsește ultimele N facturi ale unui client (pentru a le afișa pe prima pagină).
     * Aici folosim un truc JPQL pentru sortare, dar în producție am folosi Pageable.
     * Deocamdată, un simplu 'orderBy' e ok.
     */
    List<Invoice> findByCustomerIdOrderByIssueDateDesc(Long customerId);
}