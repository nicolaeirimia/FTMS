package com.ftms.service.repository;

import com.ftms.domain.fleet.Driver;
import com.ftms.domain.common.DriverStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Repository
@Scope("singleton")
public class DriverRepositoryImpl implements IDriverRepository {
    
    private static Logger logger = Logger.getLogger(DriverRepositoryImpl.class.getName());
    private Map<Integer, Driver> entities = new HashMap<>();
    private Integer nextID = 0;

    public DriverRepositoryImpl() {
        logger.info(">>> BEAN: DriverRepository instantiated!");
    }

    @Override
    public Integer getNextID() {
        return ++nextID;
    }

    @Override
    public Driver getById(Integer id) {
        return entities.get(id);
    }

    @Override
    public Driver get(Driver sample) {
        return getById(sample.getDriverId());
    }

    @Override
    public Collection<Driver> toCollection() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public Driver add(Driver entity) {
        if (entity.getDriverId() == null) {
            entity.setDriverId(getNextID());
        }
        entities.put(entity.getDriverId(), entity);
        return entity;
    }

    @Override
    public Collection<Driver> addAll(Collection<Driver> entities) {
        entities.forEach(this::add);
        return entities;
    }

    @Override
    public boolean remove(Driver entity) {
        return entities.remove(entity.getDriverId()) != null;
    }

    @Override
    public boolean removeAll(Collection<Driver> entities) {
        return entities.stream().allMatch(this::remove);
    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public Collection<Driver> findByStatus(DriverStatus status) {
        return entities.values().stream()
                .filter(d -> d.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Driver> findAvailableDrivers() {
        return findByStatus(DriverStatus.AVAILABLE);
    }

    @Override
    public Driver findByLicenseNumber(String licenseNumber) {
        return entities.values().stream()
                .filter(d -> d.getLicenseNumber().equals(licenseNumber))
                .findFirst()
                .orElse(null);
    }
}
