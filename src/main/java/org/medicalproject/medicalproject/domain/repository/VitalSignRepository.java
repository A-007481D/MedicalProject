package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.VitalSign;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VitalSignRepository extends CrudRepository<VitalSign, Long> {

    Optional<VitalSign> findLatestByPatient(Long patientId);

    List<VitalSign> findByPatientBetween(Long patientId, LocalDateTime start, LocalDateTime end);
}
