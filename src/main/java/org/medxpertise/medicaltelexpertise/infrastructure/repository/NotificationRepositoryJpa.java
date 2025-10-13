package org.medxpertise.medicaltelexpertise.infrastructure.repository;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.Notification;
import org.medxpertise.medicaltelexpertise.domain.repository.NotificationRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class NotificationRepositoryJpa implements NotificationRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Notification.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Notification> findAll() {
        return entityManager.createQuery(
                        "SELECT n FROM Notification n ORDER BY n.createdAt DESC",
                        Notification.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            entityManager.persist(notification);
            return notification;
        }
        return entityManager.merge(notification);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Notification managed = entityManager.find(Notification.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Notification> findByRecipientId(Long recipientId) {
        return entityManager.createQuery(
                        "SELECT n FROM Notification n WHERE n.recipient.id = :recipientId ORDER BY n.createdAt DESC",
                        Notification.class)
                .setParameter("recipientId", recipientId)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Notification> findUnreadByRecipientId(Long recipientId) {
        return entityManager.createQuery(
                        "SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.read = false ORDER BY n.createdAt DESC",
                        Notification.class)
                .setParameter("recipientId", recipientId)
                .getResultList();
    }
}
