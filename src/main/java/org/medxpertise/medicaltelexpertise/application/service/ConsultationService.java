package org.medxpertise.medicaltelexpertise.application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;

import java.util.List;
import java.util.Optional;

public class ConsultationService {
    private static EntityManagerFactory emf;

    static {
        emf = Persistence.createEntityManagerFactory("MedicalPU");
    }

    public Consultation createConsultation(Consultation consultation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(consultation);
            em.getTransaction().commit();
            return consultation;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error creating consultation", e);
        } finally {
            em.close();
        }
    }

    public Consultation updateConsultation(Consultation consultation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Consultation updated = em.merge(consultation);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error updating consultation", e);
        } finally {
            em.close();
        }
    }

    public Optional<Consultation> getConsultationById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Consultation.class, id));
        } finally {
            em.close();
        }
    }

    public List<Consultation> getConsultationsByGeneralist(Long generalistId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Consultation c WHERE c.generalist.id = :generalistId ORDER BY c.createdAt DESC",
                            Consultation.class)
                    .setParameter("generalistId", generalistId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Consultation> getConsultationsByGeneralistAndStatus(Long generalistId, ConsultationStatus status) {
        EntityManager em = emf.createEntityManager();
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

    public List<Consultation> getConsultationsByPatient(Long patientId) {
        EntityManager em = emf.createEntityManager();
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

    public void closeConsultation(Long consultationId) {
        EntityManager em = emf.createEntityManager();
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
        EntityManager em = emf.createEntityManager();
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

    public List<Consultation> getExpertiseRequestsBySpecialist(Long specialistId) {
        if (specialistId == null) {
            return List.of();
        }

        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT c FROM Consultation c " +
                                    "JOIN FETCH c.expertiseRequest er " +
                                    "WHERE er.specialistAssigned.id = :specialistId " +
                                    "ORDER BY er.createdAt DESC",
                            Consultation.class)
                    .setParameter("specialistId", specialistId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}