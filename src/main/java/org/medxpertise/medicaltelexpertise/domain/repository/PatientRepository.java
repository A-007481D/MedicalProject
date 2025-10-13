package org.medxpertise.medicaltelexpertise.domain.repository;

import org.medxpertise.medicaltelexpertise.domain.model.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends CrudRepository<Patient, Long> {

    Optional<Patient> findByCin(String cin);

    Optional<Patient> findBySocialSecurityNumber(String ssn);

    List<Patient> findByLastNameLike(String lastNamePrefix);

    List<Patient> findRegisteredOn(LocalDate date);
}
