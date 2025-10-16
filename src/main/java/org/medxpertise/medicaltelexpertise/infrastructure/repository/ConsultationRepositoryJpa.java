package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.persistence.EntityManager;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;
import org.medxpertise.medicaltelexpertise.domain.repository.ConsultationRepository;
import org.medxpertise.medicaltelexpertise.infrastructure.config.JpaUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ConsultationRepositoryJpa implements ConsultationRepository {

    private static final Logger logger = Logger.getLogger(ConsultationRepositoryJpa.class.getName());

    @Override
    public Optional<Consultation> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Consultation.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Consultation> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Consultation c ORDER BY c.createdAt DESC",
                            Consultation.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Consultation save(Consultation consultation) {
        logger.info("=== ConsultationRepositoryJpa.save START ===");
        logger.info("Consultation ID before save: " + consultation.getId());
        logger.info("Consultation has Patient: " + (consultation.getPatient() != null));
        logger.info("Consultation has Generalist: " + (consultation.getGeneralist() != null));
        logger.info("Consultation Motif: " + consultation.getMotif());

        EntityManager em = JpaUtil.getEntityManager();
        try {
            logger.info("EntityManager obtained, starting transaction");
            em.getTransaction().begin();

            logger.info("Transaction started, checking if consultation is new or existing");
            if (consultation.getId() == null) {
                logger.info("New consultation, calling em.persist()");
                em.persist(consultation);
                logger.info("em.persist() completed");
            } else {
                logger.info("Existing consultation, calling em.merge()");
                consultation = em.merge(consultation);
                logger.info("em.merge() completed");
            }

            logger.info("Committing transaction");
            em.getTransaction().commit();
            logger.info("Transaction committed successfully");

            logger.info("Consultation saved with ID: " + consultation.getId());
            logger.info("=== ConsultationRepositoryJpa.save END (SUCCESS) ===");
            return consultation;

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Exception in ConsultationRepositoryJpa.save: " + e.getMessage(), e);
            logger.info("Rolling back transaction due to exception");
            try {
                em.getTransaction().rollback();
                logger.info("Transaction rolled back");
            } catch (Exception rollbackEx) {
                logger.log(java.util.logging.Level.SEVERE, "Failed to rollback transaction: " + rollbackEx.getMessage(), rollbackEx);
            }
            logger.info("=== ConsultationRepositoryJpa.save END (ERROR) ===");
            throw e;
        } finally {
            logger.info("Closing EntityManager");
            em.close();
            logger.info("EntityManager closed");
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Consultation managed = em.find(Consultation.class, id);
            if (managed != null) {
                em.remove(managed);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Consultation> findByPatientId(Long patientId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Consultation c WHERE c.patient.id = :patientId ORDER BY c.createdAt DESC",
                            Consultation.class)
                    .setParameter("patientId", patientId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Consultation> findByGeneralistIdAndStatus(Long generalistId, ConsultationStatus status) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Consultation c WHERE c.generalist.id = :generalistId AND c.status = :status ORDER BY c.createdAt DESC",
                            Consultation.class)
                    .setParameter("generalistId", generalistId)
                    .setParameter("status", status)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Consultation> findBetween(LocalDateTime start, LocalDateTime end) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Consultation c WHERE c.createdAt BETWEEN :start AND :end ORDER BY c.createdAt DESC",
                            Consultation.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Consultation> findWithExpertise(Long consultationId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.createQuery(
                            "SELECT c FROM Consultation c LEFT JOIN FETCH c.expertiseRequest WHERE c.id = :id",
                            Consultation.class)
                    .setParameter("id", consultationId)
                    .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
