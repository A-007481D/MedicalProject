package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.SpecialistProfile;

import java.util.Optional;

public interface SpecialistProfileRepository extends CrudRepository<SpecialistProfile, Long> {

    Optional<SpecialistProfile> findBySpecialistId(Long specialistId);
}
