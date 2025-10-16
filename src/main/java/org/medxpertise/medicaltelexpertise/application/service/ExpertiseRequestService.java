package org.medxpertise.medicaltelexpertise.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.ExpertiseRequest;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ExpertiseStatus;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.ExpertiseRequestRepositoryJpa;
import org.medxpertise.medicaltelexpertise.domain.repository.ExpertiseRequestRepository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class ExpertiseRequestService {

    private static final Logger logger = Logger.getLogger(ExpertiseRequestService.class.getName());
    @Inject
    private ExpertiseRequestRepositoryJpa expertiseRequestRepository;

//    public ExpertiseRequestService() {
//        // For now, we'll use direct instantiation since CDI might not be fully configured
//        this.expertiseRequestRepository = new ExpertiseRequestRepositoryJpa();
//    }

    public List<ExpertiseRequest> getAllRequestsBySpecialist(Long specialistId) {
        logger.info("Getting all expertise requests for specialist ID: " + specialistId);
        return expertiseRequestRepository.findBySpecialistId(specialistId);
    }

    public ExpertiseRequest getRequestById(Long requestId) {
        logger.info("Getting expertise request by ID: " + requestId);
        return expertiseRequestRepository.findById(requestId).orElse(null);
    }

    public List<ExpertiseRequest> getRequestsBySpecialistAndStatus(Long specialistId, ExpertiseStatus status) {
        logger.info("Getting expertise requests for specialist ID: " + specialistId + " with status: " + status);
        return expertiseRequestRepository.findBySpecialistIdAndStatus(specialistId, status);
    }

    public void respondToRequest(Long requestId, String expertOpinion, String recommendations) {
        logger.info("Responding to expertise request ID: " + requestId);

        ExpertiseRequest request = expertiseRequestRepository.findById(requestId).orElse(null);
        if (request == null) {
            throw new IllegalArgumentException("Expertise request not found with ID: " + requestId);
        }

        request.markAsDone(expertOpinion, recommendations);
        expertiseRequestRepository.save(request);

        logger.info("Successfully responded to expertise request ID: " + requestId);
    }
}