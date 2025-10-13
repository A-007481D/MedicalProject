package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.persistence.EntityManager;
import org.medxpertise.medicaltelexpertise.domain.model.VitalSign;
import org.medxpertise.medicaltelexpertise.domain.repository.VitalSignRepository;
import org.medxpertise.medicaltelexpertise.infrastructure.config.JpaUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class VitalSignRepositoryJpa implements VitalSignRepository {

    private EntityManager getEntityManager() {
        return JpaUtil.getEntityManager();
    }

    @Override
    public Optional<VitalSign> findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(em.find(VitalSign.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<VitalSign> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT v FROM VitalSign v ORDER BY v.recordedAt DESC",
                            VitalSign.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public VitalSign save(VitalSign vitalSign) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            VitalSign result;
            if (vitalSign.getId() == null) {
                em.persist(vitalSign);
                result = vitalSign;
            } else {
                result = em.merge(vitalSign);
            }
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public VitalSign update(VitalSign entity) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            VitalSign managed = em.find(VitalSign.class, id);
            if (managed != null) {
                em.remove(managed);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<VitalSign> findLatestByPatient(Long patientId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT v FROM VitalSign v WHERE v.patient.id = :patientId ORDER BY v.recordedAt DESC",
                            VitalSign.class)
                    .setParameter("patientId", patientId)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public List<VitalSign> findByPatientBetween(Long patientId, LocalDateTime start, LocalDateTime end) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT v FROM VitalSign v WHERE v.patient.id = :patientId " +
                                    "AND v.recordedAt BETWEEN :start AND :end ORDER BY v.recordedAt",
                            VitalSign.class)
                    .setParameter("patientId", patientId)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
