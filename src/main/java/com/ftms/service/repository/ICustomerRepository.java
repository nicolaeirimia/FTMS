package com.ftms.service.repository;

import com.ftms.domain.customer.Customer;
import com.ftms.domain.common.CustomerStatus;
import java.util.Collection;

public interface ICustomerRepository extends IBaseRepository<Customer, Integer> {
    Collection<Customer> findByStatus(CustomerStatus status);
    Customer findByTaxId(String taxId);
}
