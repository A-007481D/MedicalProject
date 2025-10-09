package org.medicalproject.medicalproject.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medicalproject.medicalproject.domain.model.VitalSign;
import org.medicalproject.medicalproject.domain.repository.VitalSignRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VitalSignRepositoryJpa implements VitalSignRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<VitalSign> findById(Long id) {
        return Optional.ofNullable(entityManager.find(VitalSign.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<VitalSign> findAll() {
        return entityManager.createQuery(
                        "SELECT v FROM VitalSign v ORDER BY v.recordedAt DESC",
                        VitalSign.class)
                .getResultList();
    }

    @Override
    @Transactional
    public VitalSign save(VitalSign vitalSign) {
        if (vitalSign.getId() == null) {
            entityManager.persist(vitalSign);
            return vitalSign;
        }
        return entityManager.merge(vitalSign);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        VitalSign managed = entityManager.find(VitalSign.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<VitalSign> findLatestByPatient(Long patientId) {
        return entityManager.createQuery(
                        "SELECT v FROM VitalSign v WHERE v.patient.id = :patientId ORDER BY v.recordedAt DESC",
                        VitalSign.class)
                .setParameter("patientId", patientId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<VitalSign> findByPatientBetween(Long patientId, LocalDateTime start, LocalDateTime end) {
        return entityManager.createQuery(
                        "SELECT v FROM VitalSign v WHERE v.patient.id = :patientId " +
                                "AND v.recordedAt BETWEEN :start AND :end ORDER BY v.recordedAt",
                        VitalSign.class)
                .setParameter("patientId", patientId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
