package org.medicalproject.medicalproject.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medicalproject.medicalproject.domain.model.SpecialistProfile;
import org.medicalproject.medicalproject.domain.repository.SpecialistProfileRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SpecialistProfileRepositoryJpa implements SpecialistProfileRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<SpecialistProfile> findById(Long id) {
        return Optional.ofNullable(entityManager.find(SpecialistProfile.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<SpecialistProfile> findAll() {
        return entityManager.createQuery(
                        "SELECT sp FROM SpecialistProfile sp ORDER BY sp.specialist.lastName",
                        SpecialistProfile.class)
                .getResultList();
    }

    @Override
    @Transactional
    public SpecialistProfile save(SpecialistProfile profile) {
        if (profile.getId() == null) {
            entityManager.persist(profile);
            return profile;
        }
        return entityManager.merge(profile);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        SpecialistProfile managed = entityManager.find(SpecialistProfile.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<SpecialistProfile> findBySpecialistId(Long specialistId) {
        return entityManager.createQuery(
                        "SELECT sp FROM SpecialistProfile sp WHERE sp.specialist.id = :specialistId",
                        SpecialistProfile.class)
                .setParameter("specialistId", specialistId)
                .getResultStream()
                .findFirst();
    }
}
