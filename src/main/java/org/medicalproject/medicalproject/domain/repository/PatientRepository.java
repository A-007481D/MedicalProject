package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends CrudRepository<Patient, Long> {

    Optional<Patient> findByCin(String cin);

    Optional<Patient> findBySocialSecurityNumber(String ssn);

    List<Patient> findByLastNameLike(String lastNamePrefix);

    List<Patient> findRegisteredOn(LocalDate date);
}
