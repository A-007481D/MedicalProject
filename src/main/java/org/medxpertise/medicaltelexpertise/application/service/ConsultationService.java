package org.medxpertise.medicaltelexpertise.application.service;

import jakarta.persistence.EntityManager;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;
import org.medxpertise.medicaltelexpertise.domain.repository.ConsultationRepository;
import org.medxpertise.medicaltelexpertise.infrastructure.config.JpaUtil;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.ConsultationRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ConsultationService {

    private static final Logger logger = Logger.getLogger(ConsultationService.class.getName());
    private final ConsultationRepository consultationRepository = new ConsultationRepositoryJpa();

    public Consultation createConsultation(Consultation consultation) {
        logger.info("=== ConsultationService.createConsultation START ===");
        logger.info("Consultation details - Patient: " + consultation.getPatient().getFirstName() + " " + consultation.getPatient().getLastName());
        logger.info("Consultation details - Generalist: " + consultation.getGeneralist().getFirstName() + " " + consultation.getGeneralist().getLastName());
        logger.info("Consultation details - Motif: " + consultation.getMotif());

        try {
            logger.info("Calling consultationRepository.save()");
            Consultation savedConsultation = consultationRepository.save(consultation);

            if (savedConsultation != null && savedConsultation.getId() != null) {
                logger.info("Consultation saved successfully with ID: " + savedConsultation.getId());
            } else {
                logger.severe("Consultation save returned null or consultation has no ID!");
            }

            logger.info("=== ConsultationService.createConsultation END ===");
            return savedConsultation;

        } catch (Exception e) {
            logger.severe("Exception in ConsultationService.createConsultation: " + e.getMessage());
            logger.info("=== ConsultationService.createConsultation END (ERROR) ===");
            throw e;
        }
    }

    public Consultation updateConsultation(Consultation consultation) {
        return consultationRepository.save(consultation);
    }

    public Optional<Consultation> getConsultationById(Long id) {
        return consultationRepository.findById(id);
    }

    public List<Consultation> getConsultationsByGeneralist(Long generalistId) {
        return consultationRepository.findAll().stream()
                .filter(c -> c.getGeneralist() != null && c.getGeneralist().getId().equals(generalistId))
                .toList();
    }

    public List<Consultation> getConsultationsByGeneralistAndStatus(Long generalistId, ConsultationStatus status) {
        return consultationRepository.findByGeneralistIdAndStatus(generalistId, status);
    }

    public List<Consultation> getConsultationsByPatient(Long patientId) {
        return consultationRepository.findByPatientId(patientId);
    }

    public void closeConsultation(Long consultationId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            Consultation consultation = em.find(Consultation.class, consultationId);
            if (consultation != null) {
                consultation.closeConsultation();
                em.merge(consultation);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error closing consultation", e);
        } finally {
            em.close();
        }
    }

    public void markWaitingExpertise(Long consultationId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            Consultation consultation = em.find(Consultation.class, consultationId);
            if (consultation != null) {
                consultation.markWaitingExpertise();
                em.merge(consultation);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error updating consultation status", e);
        } finally {
            em.close();
        }
    }
}