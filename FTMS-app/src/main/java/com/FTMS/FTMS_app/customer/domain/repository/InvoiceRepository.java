package com.FTMS.FTMS_app.customer.domain.repository;

import com.FTMS.FTMS_app.customer.domain.model.Invoice;
import com.FTMS.FTMS_app.customer.domain.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByCustomerId(Long customerId);

    List<Invoice> findByStatus(InvoiceStatus status);

    /**
     * Găsește toate facturile restante pentru un client.
     */
    List<Invoice> findByCustomerIdAndStatus(Long customerId, InvoiceStatus status);

    /**
     * Calculează totalul datorat de un client pe facturile restante.
     */
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.customer.id = :customerId AND i.status = 'OVERDUE'")
    Double getOverdueAmountByCustomerId(Long customerId);
}