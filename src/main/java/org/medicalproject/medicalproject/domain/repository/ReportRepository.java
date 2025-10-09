package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.Report;

import java.util.List;

public interface ReportRepository extends CrudRepository<Report, Long> {

    List<Report> findByPatientId(Long patientId);

    List<Report> findByConsultationId(Long consultationId);
}
