package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.Acte;
import org.medicalproject.medicalproject.domain.model.enums.ActeType;

import java.util.List;

public interface ActeRepository extends CrudRepository<Acte, Long> {

    List<Acte> findByConsultationId(Long consultationId);

    List<Acte> findByConsultationIdAndType(Long consultationId, ActeType type);
}
