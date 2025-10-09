package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.Medication;

import java.util.List;
import java.util.Optional;

public interface MedicationRepository extends CrudRepository<Medication, Long> {

    Optional<Medication> findByName(String name);

    List<Medication> findByNameStartingWith(String prefix);
}
