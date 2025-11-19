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


    List<Invoice> findByCustomerIdAndStatus(Long customerId, InvoiceStatus status);




    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.0) FROM Invoice i " +
            "WHERE i.customer.id = :customerId " +
            "AND i.status = :status")
    Double calculateTotalAmountByStatus(
            @Param("customerId") Long customerId,
            @Param("status") InvoiceStatus status
    );


    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.0) FROM Invoice i " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate")
    Double calculateTotalRevenueBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    List<Invoice> findByCustomerIdOrderByIssueDateDesc(Long customerId);
}