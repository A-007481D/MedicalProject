package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.AuditLog;
import org.medxpertise.medicaltelexpertise.domain.repository.AuditLogRepository;
import org.medxpertise.medicaltelexpertise.infrastructure.config.JpaUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AuditLogRepositoryJpa implements AuditLogRepository {

    @PersistenceContext(unitName = "medicalPU")
    private final EntityManager entityManager;

    public AuditLogRepositoryJpa() {
        this.entityManager = JpaUtil.getEntityManager();
    }


    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<AuditLog> findById(Long id) {
        return Optional.ofNullable(entityManager.find(AuditLog.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AuditLog> findAll() {
        return entityManager.createQuery(
                        "SELECT a FROM AuditLog a ORDER BY a.timestamp DESC",
                        AuditLog.class)
                .getResultList();
    }

    @Override
    @Transactional
    public AuditLog save(AuditLog auditLog) {
        if (auditLog.getId() == null) {
            entityManager.persist(auditLog);
            return auditLog;
        }
        return entityManager.merge(auditLog);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        AuditLog managed = entityManager.find(AuditLog.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AuditLog> findByActorId(Long actorId) {
        return entityManager.createQuery(
                        "SELECT a FROM AuditLog a WHERE a.actor.id = :actorId ORDER BY a.timestamp DESC",
                        AuditLog.class)
                .setParameter("actorId", actorId)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AuditLog> findByActionCode(String actionCode) {
        return entityManager.createQuery(
                        "SELECT a FROM AuditLog a WHERE a.actionCode = :actionCode ORDER BY a.timestamp DESC",
                        AuditLog.class)
                .setParameter("actionCode", actionCode)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AuditLog> findBetween(LocalDateTime start, LocalDateTime end) {
        return entityManager.createQuery(
                        "SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :start AND :end ORDER BY a.timestamp DESC",
                        AuditLog.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
