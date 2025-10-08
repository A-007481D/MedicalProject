package org.medicalproject.medicalproject.domain.repository;

import java.util.List;
import java.util.Optional;

/**
 * Basic CRUD contract to be reused by domain repositories.
 * @param <T> entity type
 * @param <ID> identifier type
 */
public interface CrudRepository<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    T save(T entity);

    void deleteById(ID id);
}
