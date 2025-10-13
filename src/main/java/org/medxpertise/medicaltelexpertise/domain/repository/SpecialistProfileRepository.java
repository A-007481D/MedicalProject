package org.medxpertise.medicaltelexpertise.domain.repository;

import org.medxpertise.medicaltelexpertise.domain.model.SpecialistProfile;

import java.util.Optional;

public interface SpecialistProfileRepository extends CrudRepository<SpecialistProfile, Long> {

    Optional<SpecialistProfile> findBySpecialistId(Long specialistId);
}
