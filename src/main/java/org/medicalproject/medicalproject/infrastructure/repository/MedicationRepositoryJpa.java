package org.medicalproject.medicalproject.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medicalproject.medicalproject.domain.model.Medication;
import org.medicalproject.medicalproject.domain.repository.MedicationRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MedicationRepositoryJpa implements MedicationRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Medication> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Medication.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Medication> findAll() {
        return entityManager.createQuery(
                        "SELECT m FROM Medication m ORDER BY m.name",
                        Medication.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Medication save(Medication medication) {
        if (medication.getId() == null) {
            entityManager.persist(medication);
            return medication;
        }
        return entityManager.merge(medication);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Medication managed = entityManager.find(Medication.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Medication> findByName(String name) {
        return entityManager.createQuery(
                        "SELECT m FROM Medication m WHERE LOWER(m.name) = :name",
                        Medication.class)
                .setParameter("name", name.toLowerCase())
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Medication> findByNameStartingWith(String prefix) {
        String queryPrefix = prefix == null ? "" : prefix.toLowerCase();
        return entityManager.createQuery(
                        "SELECT m FROM Medication m WHERE LOWER(m.name) LIKE :prefix ORDER BY m.name",
                        Medication.class)
                .setParameter("prefix", queryPrefix + "%")
                .getResultList();
    }
}
