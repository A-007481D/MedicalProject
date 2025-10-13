package org.medxpertise.medicaltelexpertise.infrastructure.repository;



import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.Timeslot;
import org.medxpertise.medicaltelexpertise.domain.model.enums.TimeslotStatus;
import org.medxpertise.medicaltelexpertise.domain.repository.TimeslotRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TimeslotRepositoryJpa implements TimeslotRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Timeslot> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Timeslot.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Timeslot> findAll() {
        return entityManager.createQuery(
                        "SELECT t FROM Timeslot t ORDER BY t.start",
                        Timeslot.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Timeslot save(Timeslot timeslot) {
        if (timeslot.getId() == null) {
            entityManager.persist(timeslot);
            return timeslot;
        }
        return entityManager.merge(timeslot);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Timeslot managed = entityManager.find(Timeslot.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Timeslot> findByProfileId(Long profileId) {
        return entityManager.createQuery(
                        "SELECT t FROM Timeslot t WHERE t.profile.id = :profileId ORDER BY t.start",
                        Timeslot.class)
                .setParameter("profileId", profileId)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Timeslot> findAvailableBetween(Long profileId, LocalDateTime start, LocalDateTime end) {
        return entityManager.createQuery(
                        "SELECT t FROM Timeslot t WHERE t.profile.id = :profileId " +
                                "AND t.status = :status AND t.start >= :start AND t.end <= :end ORDER BY t.start",
                        Timeslot.class)
                .setParameter("profileId", profileId)
                .setParameter("status", TimeslotStatus.AVAILABLE)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Timeslot> findByStatus(TimeslotStatus status) {
        return entityManager.createQuery(
                        "SELECT t FROM Timeslot t WHERE t.status = :status ORDER BY t.start",
                        Timeslot.class)
                .setParameter("status", status)
                .getResultList();
    }
}

