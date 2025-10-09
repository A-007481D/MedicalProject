package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.Generalist;

import java.util.Optional;

public interface GeneralistRepository extends CrudRepository<Generalist, Long> {

    Optional<Generalist> findByEmail(String email);
}
