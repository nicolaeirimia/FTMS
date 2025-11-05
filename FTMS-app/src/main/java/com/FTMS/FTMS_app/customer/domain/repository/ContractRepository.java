package com.FTMS.FTMS_app.customer.domain.repository;

import com.FTMS.FTMS_app.customer.domain.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    /**
     * Găsește contractele care expiră curând (pentru alerte).
     */
    List<Contract> findByEndDateBetween(LocalDate today, LocalDate nearFuture);
}