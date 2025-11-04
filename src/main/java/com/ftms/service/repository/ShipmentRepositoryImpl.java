package com.ftms.service.repository;

import com.ftms.domain.shipment.Shipment;
import com.ftms.domain.common.ShipmentStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Shipment Repository Implementation
 * In-memory storage for Shipments
 */
@Repository
@Scope("singleton")
public class ShipmentRepositoryImpl implements IShipmentRepository {
    
    private static Logger logger = Logger.getLogger(ShipmentRepositoryImpl.class.getName());
    
    private Map<Integer, Shipment> entities = new HashMap<>();
    private Integer nextID = 0;

    public ShipmentRepositoryImpl() {
        logger.info(">>> BEAN: ShipmentRepository instantiated!");
    }

    @Override
    public Integer getNextID() {
        return ++nextID;
    }

    @Override
    public Shipment getById(Integer id) {
        return entities.get(id);
    }

    @Override
    public Shipment get(Shipment sample) {
        return getById(sample.getShipmentId());
    }

    @Override
    public Collection<Shipment> toCollection() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public Shipment add(Shipment entity) {
        if (entity.getShipmentId() == null) {
            entity.setShipmentId(getNextID());
        }
        entities.put(entity.getShipmentId(), entity);
        logger.info("Added shipment: " + entity);
        return entity;
    }

    @Override
    public Collection<Shipment> addAll(Collection<Shipment> entities) {
        entities.forEach(this::add);
        return entities;
    }

    @Override
    public boolean remove(Shipment entity) {
        if (entities.containsValue(entity)) {
            entities.remove(entity.getShipmentId());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<Shipment> entities) {
        return entities.stream().allMatch(this::remove);
    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public Collection<Shipment> findByStatus(ShipmentStatus status) {
        return entities.values().stream()
                .filter(s -> s.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Shipment> findByCustomerId(Integer customerId) {
        return entities.values().stream()
                .filter(s -> s.getCustomer() != null && 
                           s.getCustomer().getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    @Override
    public Shipment findByReferenceNumber(String referenceNumber) {
        return entities.values().stream()
                .filter(s -> s.getReferenceNumber().equals(referenceNumber))
                .findFirst()
                .orElse(null);
    }
}
