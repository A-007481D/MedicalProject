package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.Prescription;
import org.medxpertise.medicaltelexpertise.domain.repository.PrescriptionRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PrescriptionRepositoryJpa implements PrescriptionRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Prescription> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Prescription.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Prescription> findAll() {
        return entityManager.createQuery(
                        "SELECT p FROM Prescription p ORDER BY p.issuedAt DESC",
                        Prescription.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Prescription save(Prescription prescription) {
        if (prescription.getId() == null) {
            entityManager.persist(prescription);
            return prescription;
        }
        return entityManager.merge(prescription);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Prescription managed = entityManager.find(Prescription.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Prescription> findByConsultationId(Long consultationId) {
        return entityManager.createQuery(
                        "SELECT p FROM Prescription p WHERE p.consultation.id = :consultationId ORDER BY p.issuedAt DESC",
                        Prescription.class)
                .setParameter("consultationId", consultationId)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Prescription> findByPatientId(Long patientId) {
        return entityManager.createQuery(
                        "SELECT DISTINCT p FROM Prescription p " +
                                "JOIN p.consultation c " +
                                "WHERE c.patient.id = :patientId ORDER BY p.issuedAt DESC",
                        Prescription.class)
                .setParameter("patientId", patientId)
                .getResultList();
    }
}
