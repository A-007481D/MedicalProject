package org.medxpertise.medicaltelexpertise.domain.repository;

import org.medxpertise.medicaltelexpertise.domain.model.Report;

import java.util.List;

public interface ReportRepository extends CrudRepository<Report, Long> {

    List<Report> findByPatientId(Long patientId);

    List<Report> findByConsultationId(Long consultationId);
}
