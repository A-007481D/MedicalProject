package org.medxpertise.medicaltelexpertise.infrastructure.repository;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.QueueEntry;
import org.medxpertise.medicaltelexpertise.domain.model.enums.QueueStatus;
import org.medxpertise.medicaltelexpertise.domain.repository.QueueEntryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class QueueEntryRepositoryJpa implements QueueEntryRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<QueueEntry> findById(Long id) {
        return Optional.ofNullable(entityManager.find(QueueEntry.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<QueueEntry> findAll() {
        return entityManager.createQuery(
                        "SELECT q FROM QueueEntry q ORDER BY q.arrivalTime",
                        QueueEntry.class)
                .getResultList();
    }

    @Override
    @Transactional
    public QueueEntry save(QueueEntry queueEntry) {
        if (queueEntry.getId() == null) {
            entityManager.persist(queueEntry);
            return queueEntry;
        }
        return entityManager.merge(queueEntry);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        QueueEntry managed = entityManager.find(QueueEntry.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<QueueEntry> findActiveQueue() {
        return entityManager.createQuery(
                        "SELECT q FROM QueueEntry q WHERE q.status <> :done ORDER BY q.arrivalTime",
                        QueueEntry.class)
                .setParameter("done", QueueStatus.DONE)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<QueueEntry> findByPatientId(Long patientId) {
        return entityManager.createQuery(
                        "SELECT q FROM QueueEntry q WHERE q.patient.id = :patientId ORDER BY q.arrivalTime DESC",
                        QueueEntry.class)
                .setParameter("patientId", patientId)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<QueueEntry> findCurrentForPatient(Long patientId, List<QueueStatus> activeStatuses) {
        List<QueueStatus> statuses = (activeStatuses == null || activeStatuses.isEmpty())
                ? List.of(QueueStatus.WAITING, QueueStatus.IN_PROGRESS)
                : activeStatuses;

        return entityManager.createQuery(
                        "SELECT q FROM QueueEntry q " +
                                "WHERE q.patient.id = :patientId AND q.status IN :statuses " +
                                "ORDER BY q.arrivalTime DESC",
                        QueueEntry.class)
                .setParameter("patientId", patientId)
                .setParameter("statuses", statuses)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
