package org.medxpertise.medicaltelexpertise.infrastructure.repository;



import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.Report;
import org.medxpertise.medicaltelexpertise.domain.repository.ReportRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReportRepositoryJpa implements ReportRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Report> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Report.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Report> findAll() {
        return entityManager.createQuery(
                        "SELECT r FROM Report r ORDER BY r.uploadedAt DESC",
                        Report.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Report save(Report report) {
        if (report.getId() == null) {
            entityManager.persist(report);
            return report;
        }
        return entityManager.merge(report);
    }

    public Report update(Report entity) {
        return null;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Report managed = entityManager.find(Report.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Report> findByPatientId(Long patientId) {
        return entityManager.createQuery(
                        "SELECT r FROM Report r WHERE r.patient.id = :patientId ORDER BY r.uploadedAt DESC",
                        Report.class)
                .setParameter("patientId", patientId)
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Report> findByConsultationId(Long consultationId) {
        return entityManager.createQuery(
                        "SELECT r FROM Report r WHERE r.consultation.id = :consultationId ORDER BY r.uploadedAt DESC",
                        Report.class)
                .setParameter("consultationId", consultationId)
                .getResultList();
    }
}
