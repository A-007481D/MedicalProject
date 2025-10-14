package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;
import org.medxpertise.medicaltelexpertise.domain.repository.ConsultationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ConsultationRepositoryJpa implements ConsultationRepository {

    @PersistenceContext(unitName = "MedicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Consultation> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Consultation.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Consultation> findAll() {
        return entityManager.createQuery(
                        "SELECT c FROM Consultation c ORDER BY c.createdAt DESC",
                        Consultation.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Consultation save(Consultation consultation) {
        if (consultation.getId() == null) {
            entityManager.persist(consultation);
            return consultation;
        }
        return entityManager.merge(consultation);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Consultation managed = entityManager.find(Consultation.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Consultation> findByPatientId(Long patientId) {
        return entityManager.createQuery(
                        "SELECT c FROM Consultation c WHERE c.patient.id = :patientId ORDER BY c.createdAt DESC",
                        Consultation.class)
                .setParameter("patientId", patientId)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Consultation> findByGeneralistIdAndStatus(Long generalistId, ConsultationStatus status) {
        return entityManager.createQuery(
                        "SELECT c FROM Consultation c WHERE c.generalist.id = :generalistId AND c.status = :status ORDER BY c.createdAt DESC",
                        Consultation.class)
                .setParameter("generalistId", generalistId)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Consultation> findBetween(LocalDateTime start, LocalDateTime end) {
        return entityManager.createQuery(
                        "SELECT c FROM Consultation c WHERE c.createdAt >= :start AND c.createdAt <= :end ORDER BY c.createdAt",
                        Consultation.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Consultation> findWithExpertise(Long consultationId) {
        EntityGraph<Consultation> graph = entityManager.createEntityGraph(Consultation.class);
        graph.addAttributeNodes("expertiseRequest");
        graph.addSubgraph("expertiseRequest").addAttributeNodes("specialistAssigned");

        return entityManager.createQuery(
                        "SELECT c FROM Consultation c WHERE c.id = :id",
                        Consultation.class)
                .setParameter("id", consultationId)
                .setHint("jakarta.persistence.fetchgraph", graph)
                .getResultStream()
                .findFirst();
    }
}
