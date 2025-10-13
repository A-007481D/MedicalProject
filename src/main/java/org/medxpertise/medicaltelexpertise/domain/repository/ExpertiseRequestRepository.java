package org.medxpertise.medicaltelexpertise.domain.repository;

import org.medxpertise.medicaltelexpertise.domain.model.ExpertiseRequest;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ExpertiseStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.PriorityLevel;

import java.util.List;
import java.util.Optional;

public interface ExpertiseRequestRepository extends CrudRepository<ExpertiseRequest, Long> {

    List<ExpertiseRequest> findByStatus(ExpertiseStatus status);

    List<ExpertiseRequest> findByPriority(PriorityLevel priorityLevel);

    Optional<ExpertiseRequest> findByConsultationId(Long consultationId);
}
