package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.persistence.EntityManager;
import org.medxpertise.medicaltelexpertise.domain.model.QueueEntry;
import org.medxpertise.medicaltelexpertise.domain.model.enums.QueueStatus;
import org.medxpertise.medicaltelexpertise.domain.repository.QueueEntryRepository;
import org.medxpertise.medicaltelexpertise.infrastructure.config.JpaUtil;

import java.util.List;
import java.util.Optional;

public class QueueEntryRepositoryJpa implements QueueEntryRepository {

    private EntityManager getEntityManager() {
        return JpaUtil.getEntityManager();
    }

    @Override
    public Optional<QueueEntry> findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(em.find(QueueEntry.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<QueueEntry> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT q FROM QueueEntry q ORDER BY q.arrivalTime",
                            QueueEntry.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public QueueEntry save(QueueEntry queueEntry) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            QueueEntry result;
            if (queueEntry.getId() == null) {
                em.persist(queueEntry);
                result = queueEntry;
            } else {
                result = em.merge(queueEntry);
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

    @Override
    public void deleteById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            QueueEntry managed = em.find(QueueEntry.class, id);
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
    public List<QueueEntry> findActiveQueue() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT q FROM QueueEntry q WHERE q.status <> :done ORDER BY q.arrivalTime",
                            QueueEntry.class)
                    .setParameter("done", QueueStatus.DONE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<QueueEntry> findByPatientId(Long patientId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT q FROM QueueEntry q WHERE q.patient.id = :patientId ORDER BY q.arrivalTime DESC",
                            QueueEntry.class)
                    .setParameter("patientId", patientId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<QueueEntry> findCurrentForPatient(Long patientId, List<QueueStatus> activeStatuses) {
        EntityManager em = getEntityManager();
        try {
            List<QueueStatus> statuses = (activeStatuses == null || activeStatuses.isEmpty())
                    ? List.of(QueueStatus.WAITING, QueueStatus.IN_PROGRESS)
                    : activeStatuses;

            return em.createQuery(
                            "SELECT q FROM QueueEntry q " +
                                    "WHERE q.patient.id = :patientId AND q.status IN :statuses " +
                                    "ORDER BY q.arrivalTime DESC",
                            QueueEntry.class)
                    .setParameter("patientId", patientId)
                    .setParameter("statuses", statuses)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }
}
