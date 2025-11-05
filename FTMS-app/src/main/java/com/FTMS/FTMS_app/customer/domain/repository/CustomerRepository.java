package com.FTMS.FTMS_app.customer.domain.repository;

import com.FTMS.FTMS_app.customer.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByTaxIdNumber(String taxIdNumber);
    Optional<Customer> findByPrimaryContactEmail(String email);
    Optional<Customer> findByRegistrationNumber(String regNumber);
}