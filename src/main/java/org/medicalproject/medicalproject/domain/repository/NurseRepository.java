package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.Nurse;

import java.util.Optional;

public interface NurseRepository extends CrudRepository<Nurse, Long> {

    Optional<Nurse> findByEmail(String email);
}
