package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.Prescription;

import java.util.List;

public interface PrescriptionRepository extends CrudRepository<Prescription, Long> {

    List<Prescription> findByConsultationId(Long consultationId);

    List<Prescription> findByPatientId(Long patientId);
}
