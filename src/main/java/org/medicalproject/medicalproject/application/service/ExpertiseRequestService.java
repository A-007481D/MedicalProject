package org.medicalproject.medicalproject.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.medicalproject.medicalproject.application.service.exception.BusinessRuleException;
import org.medicalproject.medicalproject.application.service.exception.ResourceNotFoundException;
import org.medicalproject.medicalproject.domain.model.AuditLog;
import org.medicalproject.medicalproject.domain.model.Consultation;
import org.medicalproject.medicalproject.domain.model.ExpertiseRequest;
import org.medicalproject.medicalproject.domain.model.Notification;
import org.medicalproject.medicalproject.domain.model.Specialist;
import org.medicalproject.medicalproject.domain.model.SpecialistProfile;
import org.medicalproject.medicalproject.domain.model.Timeslot;
import org.medicalproject.medicalproject.domain.model.enums.ExpertiseStatus;
import org.medicalproject.medicalproject.domain.model.enums.PriorityLevel;
import org.medicalproject.medicalproject.domain.model.enums.TimeslotStatus;
import org.medicalproject.medicalproject.domain.repository.AuditLogRepository;
import org.medicalproject.medicalproject.domain.repository.ConsultationRepository;
import org.medicalproject.medicalproject.domain.repository.ExpertiseRequestRepository;
import org.medicalproject.medicalproject.domain.repository.NotificationRepository;
import org.medicalproject.medicalproject.domain.repository.SpecialistProfileRepository;
import org.medicalproject.medicalproject.domain.repository.TimeslotRepository;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ExpertiseRequestService {

    private final ConsultationRepository consultationRepository;
    private final ExpertiseRequestRepository expertiseRequestRepository;
    private final SpecialistProfileRepository specialistProfileRepository;
    private final TimeslotRepository timeslotRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;

    @Inject
    public ExpertiseRequestService(ConsultationRepository consultationRepository,
                                   ExpertiseRequestRepository expertiseRequestRepository,
                                   SpecialistProfileRepository specialistProfileRepository,
                                   TimeslotRepository timeslotRepository,
                                   NotificationRepository notificationRepository,
                                   AuditLogRepository auditLogRepository) {
        this.consultationRepository = consultationRepository;
        this.expertiseRequestRepository = expertiseRequestRepository;
        this.specialistProfileRepository = specialistProfileRepository;
        this.timeslotRepository = timeslotRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public ExpertiseRequest requestExpertise(Long consultationId,
                                             String specializationNeeded,
                                             String question,
                                             PriorityLevel priorityLevel) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id " + consultationId));

        if (consultation.getExpertiseRequest() != null
                && consultation.getExpertiseRequest().getStatus() != ExpertiseStatus.DONE) {
            throw new BusinessRuleException("Consultation already has an active expertise request");
        }

        ExpertiseRequest request = new ExpertiseRequest();
        request.setConsultation(consultation);
        request.setSpecializationNeeded(specializationNeeded);
        request.setQuestion(question);
        request.setPriority(priorityLevel == null ? PriorityLevel.NORMAL : priorityLevel);
        request.setStatus(ExpertiseStatus.PENDING);
        request.setRequestedAt(LocalDateTime.now());

        ExpertiseRequest saved = expertiseRequestRepository.save(request);
        consultation.setExpertiseRequest(saved);
        consultation.markWaitingExpertise();
        consultationRepository.save(consultation);

        auditLogRepository.save(new AuditLog("EXPERTISE_REQUESTED",
                "Expertise requested for consultation " + consultationId,
                consultation.getGeneralist()));

        return saved;
    }

    @Transactional
    public ExpertiseRequest assignSpecialist(Long expertiseRequestId,
                                             Long specialistProfileId,
                                             Long timeslotId) {
        ExpertiseRequest request = expertiseRequestRepository.findById(expertiseRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Expertise request not found with id " + expertiseRequestId));

        SpecialistProfile profile = specialistProfileRepository.findById(specialistProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist profile not found with id " + specialistProfileId));

        Specialist specialist = profile.getSpecialist();
        if (specialist == null) {
            throw new BusinessRuleException("Specialist profile " + specialistProfileId + " has no specialist bound");
        }

        Timeslot timeslot = null;
        if (timeslotId != null) {
            timeslot = timeslotRepository.findById(timeslotId)
                    .orElseThrow(() -> new ResourceNotFoundException("Timeslot not found with id " + timeslotId));
            if (!timeslot.getProfile().getId().equals(specialistProfileId)) {
                throw new BusinessRuleException("Timeslot does not belong to the provided specialist profile");
            }
            if (timeslot.getStatus() != TimeslotStatus.AVAILABLE) {
                throw new BusinessRuleException("Timeslot " + timeslotId + " is not available");
            }
            timeslot.reserve();
            timeslotRepository.save(timeslot);
            request.setScheduledSlotStart(timeslot.getStart());
            request.setScheduledSlotEnd(timeslot.getEnd());
        }

        request.setSpecialistAssigned(specialist);
        request.setStatus(ExpertiseStatus.PENDING);

        ExpertiseRequest saved = expertiseRequestRepository.save(request);

        Notification notification = new Notification("New expertise request assigned (ID: " + saved.getId() + ")");
        notification.setRecipient(specialist);
        notificationRepository.save(notification);

        auditLogRepository.save(new AuditLog("EXPERTISE_ASSIGNED",
                "Expertise request " + saved.getId() + " assigned to specialist " + specialist.getId(),
                consultationRepository.findById(request.getConsultation().getId())
                        .map(Consultation::getGeneralist)
                        .orElse(null)));

        return saved;
    }

    @Transactional
    public ExpertiseRequest markExpertiseDone(Long expertiseRequestId,
                                              String opinion,
                                              String recommendations) {
        ExpertiseRequest request = expertiseRequestRepository.findById(expertiseRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Expertise request not found with id " + expertiseRequestId));

        if (request.getStatus() == ExpertiseStatus.DONE) {
            return request;
        }

        request.markAsDone(opinion, recommendations);
        ExpertiseRequest saved = expertiseRequestRepository.save(request);

        Notification notification = new Notification("Expertise request " + expertiseRequestId + " completed");
        notification.setRecipient(request.getConsultation().getGeneralist());
        notificationRepository.save(notification);

        auditLogRepository.save(new AuditLog("EXPERTISE_DONE",
                "Expertise request " + expertiseRequestId + " completed",
                request.getSpecialistAssigned()));

        return saved;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public ExpertiseRequest getExpertiseRequest(Long expertiseRequestId) {
        return expertiseRequestRepository.findById(expertiseRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Expertise request not found with id " + expertiseRequestId));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ExpertiseRequest> getRequestsByStatus(ExpertiseStatus status) {
        return expertiseRequestRepository.findByStatus(status);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ExpertiseRequest> getRequestsByPriority(PriorityLevel priorityLevel) {
        return expertiseRequestRepository.findByPriority(priorityLevel);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public ExpertiseRequest getRequestByConsultation(Long consultationId) {
        return expertiseRequestRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("No expertise request for consultation " + consultationId));
    }
}
