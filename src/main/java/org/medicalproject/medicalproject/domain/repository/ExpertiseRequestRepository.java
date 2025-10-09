package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.ExpertiseRequest;
import org.medicalproject.medicalproject.domain.model.enums.ExpertiseStatus;
import org.medicalproject.medicalproject.domain.model.enums.PriorityLevel;

import java.util.List;
import java.util.Optional;

public interface ExpertiseRequestRepository extends CrudRepository<ExpertiseRequest, Long> {

    List<ExpertiseRequest> findByStatus(ExpertiseStatus status);

    List<ExpertiseRequest> findByPriority(PriorityLevel priorityLevel);

    Optional<ExpertiseRequest> findByConsultationId(Long consultationId);
}
