package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.Nurse;
import org.medxpertise.medicaltelexpertise.domain.repository.NurseRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class NurseRepositoryJpa implements NurseRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Nurse> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Nurse.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Nurse> findAll() {
        return entityManager.createQuery(
                        "SELECT n FROM Nurse n ORDER BY n.lastName, n.firstName",
                        Nurse.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Nurse save(Nurse nurse) {
        if (nurse.getId() == null) {
            entityManager.persist(nurse);
            return nurse;
        }
        return entityManager.merge(nurse);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Nurse managed = entityManager.find(Nurse.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Nurse> findByEmail(String email) {
        return entityManager.createQuery(
                        "SELECT n FROM Nurse n WHERE LOWER(n.email) = :email",
                        Nurse.class)
                .setParameter("email", email.toLowerCase())
                .getResultStream()
                .findFirst();
    }
}
