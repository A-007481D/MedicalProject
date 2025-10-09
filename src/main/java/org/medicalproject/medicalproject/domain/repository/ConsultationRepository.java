package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.Consultation;
import org.medicalproject.medicalproject.domain.model.enums.ConsultationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends CrudRepository<Consultation, Long> {

    List<Consultation> findByPatientId(Long patientId);

    List<Consultation> findByGeneralistIdAndStatus(Long generalistId, ConsultationStatus status);

    List<Consultation> findBetween(LocalDateTime start, LocalDateTime end);

    Optional<Consultation> findWithExpertise(Long consultationId);
}
