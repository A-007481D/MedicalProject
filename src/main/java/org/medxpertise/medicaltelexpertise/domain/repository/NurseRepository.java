package org.medxpertise.medicaltelexpertise.domain.repository;

import org.medxpertise.medicaltelexpertise.domain.model.Nurse;
import org.medxpertise.medicaltelexpertise.domain.repository.CrudRepository;

import java.util.Optional;

public interface NurseRepository extends CrudRepository<Nurse, Long> {

    Optional<Nurse> findByEmail(String email);
}
