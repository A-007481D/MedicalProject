package org.medxpertise.medicaltelexpertise.domain.repository;


import org.medxpertise.medicaltelexpertise.domain.model.Generalist;

import java.util.Optional;

public interface GeneralistRepository extends CrudRepository<Generalist, Long> {

    Optional<Generalist> findByEmail(String email);
}