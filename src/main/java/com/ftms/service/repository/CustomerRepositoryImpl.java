package com.ftms.service.repository;

import com.ftms.domain.customer.Customer;
import com.ftms.domain.common.CustomerStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Repository
@Scope("singleton")
public class CustomerRepositoryImpl implements ICustomerRepository {
    
    private static Logger logger = Logger.getLogger(CustomerRepositoryImpl.class.getName());
    private Map<Integer, Customer> entities = new HashMap<>();
    private Integer nextID = 0;

    public CustomerRepositoryImpl() {
        logger.info(">>> BEAN: CustomerRepository instantiated!");
    }

    @Override
    public Integer getNextID() {
        return ++nextID;
    }

    @Override
    public Customer getById(Integer id) {
        return entities.get(id);
    }

    @Override
    public Customer get(Customer sample) {
        return getById(sample.getCustomerId());
    }

    @Override
    public Collection<Customer> toCollection() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public Customer add(Customer entity) {
        if (entity.getCustomerId() == null) {
            entity.setCustomerId(getNextID());
        }
        entities.put(entity.getCustomerId(), entity);
        return entity;
    }

    @Override
    public Collection<Customer> addAll(Collection<Customer> entities) {
        entities.forEach(this::add);
        return entities;
    }

    @Override
    public boolean remove(Customer entity) {
        return entities.remove(entity.getCustomerId()) != null;
    }

    @Override
    public boolean removeAll(Collection<Customer> entities) {
        return entities.stream().allMatch(this::remove);
    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public Collection<Customer> findByStatus(CustomerStatus status) {
        return entities.values().stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public Customer findByTaxId(String taxId) {
        return entities.values().stream()
                .filter(c -> c.getTaxIdentificationNumber().equals(taxId))
                .findFirst()
                .orElse(null);
    }
}
