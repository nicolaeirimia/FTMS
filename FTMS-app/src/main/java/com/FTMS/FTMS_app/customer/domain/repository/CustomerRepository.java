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


    Optional<Customer> findByTaxIdNumber(String taxIdNumber);

    Optional<Customer> findByPrimaryContactEmail(String email);

    Optional<Customer> findByRegistrationNumber(String regNumber);



    @Query("SELECT c FROM Customer c " +
            "WHERE LOWER(c.companyName) LIKE LOWER(CONCAT('%', :nameFragment, '%')) " +
            "AND c.status = 'ACTIVE'")
    List<Customer> searchActiveCustomersByName(@Param("nameFragment") String nameFragment);



    @Query("SELECT c FROM Customer c " +
            "WHERE c.billingAddress.city = :city " +
            "AND c.category = :category")
    List<Customer> findCustomersByCityAndCategory(
            @Param("city") String city,
            @Param("category") CustomerCategory category
    );



    @Query("SELECT c FROM Customer c " +
            "WHERE c.status = :status " +
            "AND c.creditLimit > :threshold")
    List<Customer> findRiskyCustomers(
            @Param("status") CustomerStatus status,
            @Param("threshold") double creditLimitThreshold
    );
}