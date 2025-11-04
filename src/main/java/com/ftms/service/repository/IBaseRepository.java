package com.ftms.service.repository;

import java.util.Collection;

/**
 * Base Repository Interface
 * Generic CRUD operations for all aggregates
 */
public interface IBaseRepository<T, ID> {
    ID getNextID();
    T getById(ID id);
    T get(T sample);
    Collection<T> toCollection();
    T add(T entity);
    Collection<T> addAll(Collection<T> entities);
    boolean remove(T entity);
    boolean removeAll(Collection<T> entities);
    int size();
}
