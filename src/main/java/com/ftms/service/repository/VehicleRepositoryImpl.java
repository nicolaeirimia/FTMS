package com.ftms.service.repository;

import com.ftms.domain.fleet.Vehicle;
import com.ftms.domain.common.VehicleStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Repository
@Scope("singleton")
public class VehicleRepositoryImpl implements IVehicleRepository {
    
    private static Logger logger = Logger.getLogger(VehicleRepositoryImpl.class.getName());
    private Map<Integer, Vehicle> entities = new HashMap<>();
    private Integer nextID = 0;

    public VehicleRepositoryImpl() {
        logger.info(">>> BEAN: VehicleRepository instantiated!");
    }

    @Override
    public Integer getNextID() {
        return ++nextID;
    }

    @Override
    public Vehicle getById(Integer id) {
        return entities.get(id);
    }

    @Override
    public Vehicle get(Vehicle sample) {
        return getById(sample.getVehicleId());
    }

    @Override
    public Collection<Vehicle> toCollection() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public Vehicle add(Vehicle entity) {
        if (entity.getVehicleId() == null) {
            entity.setVehicleId(getNextID());
        }
        entities.put(entity.getVehicleId(), entity);
        return entity;
    }

    @Override
    public Collection<Vehicle> addAll(Collection<Vehicle> entities) {
        entities.forEach(this::add);
        return entities;
    }

    @Override
    public boolean remove(Vehicle entity) {
        return entities.remove(entity.getVehicleId()) != null;
    }

    @Override
    public boolean removeAll(Collection<Vehicle> entities) {
        return entities.stream().allMatch(this::remove);
    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public Collection<Vehicle> findByStatus(VehicleStatus status) {
        return entities.values().stream()
                .filter(v -> v.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Vehicle> findAvailableVehicles() {
        return findByStatus(VehicleStatus.AVAILABLE);
    }

    @Override
    public Vehicle findByRegistrationNumber(String registrationNumber) {
        return entities.values().stream()
                .filter(v -> v.getRegistrationNumber().equals(registrationNumber))
                .findFirst()
                .orElse(null);
    }
}
