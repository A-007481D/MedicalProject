package org.medxpertise.medicaltelexpertise.domain.repository;


import org.medxpertise.medicaltelexpertise.domain.model.Acte;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ActeType;

import java.util.List;

public interface ActeRepository extends CrudRepository<Acte, Long> {

    List<Acte> findByConsultationId(Long consultationId);

    List<Acte> findByConsultationIdAndType(Long consultationId, ActeType type);
}
