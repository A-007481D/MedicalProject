package org.medxpertise.medicaltelexpertise.infrastructure.repository;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.ExpertiseRequest;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ExpertiseStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.PriorityLevel;
import org.medxpertise.medicaltelexpertise.domain.repository.ExpertiseRequestRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ExpertiseRequestRepositoryJpa implements ExpertiseRequestRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<ExpertiseRequest> findById(Long id) {
        return Optional.ofNullable(entityManager.find(ExpertiseRequest.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ExpertiseRequest> findAll() {
        return entityManager.createQuery(
                        "SELECT er FROM ExpertiseRequest er ORDER BY er.requestedAt DESC",
                        ExpertiseRequest.class)
                .getResultList();
    }

    @Override
    @Transactional
    public ExpertiseRequest save(ExpertiseRequest expertiseRequest) {
        if (expertiseRequest.getId() == null) {
            entityManager.persist(expertiseRequest);
            return expertiseRequest;
        }
        return entityManager.merge(expertiseRequest);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ExpertiseRequest managed = entityManager.find(ExpertiseRequest.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ExpertiseRequest> findByStatus(ExpertiseStatus status) {
        return entityManager.createQuery(
                        "SELECT er FROM ExpertiseRequest er WHERE er.status = :status ORDER BY er.requestedAt DESC",
                        ExpertiseRequest.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ExpertiseRequest> findByPriority(PriorityLevel priorityLevel) {
        return entityManager.createQuery(
                        "SELECT er FROM ExpertiseRequest er WHERE er.priority = :priority ORDER BY er.requestedAt DESC",
                        ExpertiseRequest.class)
                .setParameter("priority", priorityLevel)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<ExpertiseRequest> findByConsultationId(Long consultationId) {
        return entityManager.createQuery(
                        "SELECT er FROM ExpertiseRequest er WHERE er.consultation.id = :consultationId",
                        ExpertiseRequest.class)
                .setParameter("consultationId", consultationId)
                .getResultStream()
                .findFirst();
    }
}
