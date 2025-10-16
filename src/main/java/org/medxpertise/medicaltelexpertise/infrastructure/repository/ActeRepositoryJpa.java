package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.infrastructure.config.qualifier.AppEntityManager;
import org.medxpertise.medicaltelexpertise.domain.model.Acte;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ActeType;
import org.medxpertise.medicaltelexpertise.domain.repository.ActeRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ActeRepositoryJpa implements ActeRepository {

    @Inject
    @AppEntityManager
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Acte> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Acte.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Acte> findAll() {
        return entityManager.createQuery(
                        "SELECT a FROM Acte a ORDER BY a.consultation.createdAt DESC",
                        Acte.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Acte save(Acte acte) {
        if (acte.getId() == null) {
            entityManager.persist(acte);
            return acte;
        }
        return entityManager.merge(acte);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Acte managed = entityManager.find(Acte.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Acte> findByConsultationId(Long consultationId) {
        return entityManager.createQuery(
                        "SELECT a FROM Acte a WHERE a.consultation.id = :consultationId ORDER BY a.id",
                        Acte.class)
                .setParameter("consultationId", consultationId)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Acte> findByConsultationIdAndType(Long consultationId, ActeType type) {
        return entityManager.createQuery(
                        "SELECT a FROM Acte a WHERE a.consultation.id = :consultationId AND a.type = :type ORDER BY a.id",
                        Acte.class)
                .setParameter("consultationId", consultationId)
                .setParameter("type", type)
                .getResultList();
    }
}
