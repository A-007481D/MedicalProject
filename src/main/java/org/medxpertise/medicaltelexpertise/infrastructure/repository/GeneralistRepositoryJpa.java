package org.medxpertise.medicaltelexpertise.infrastructure.repository;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.Generalist;
import org.medxpertise.medicaltelexpertise.domain.repository.GeneralistRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GeneralistRepositoryJpa implements GeneralistRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Generalist> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Generalist.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Generalist> findAll() {
        return entityManager.createQuery(
                        "SELECT g FROM Generalist g ORDER BY g.lastName, g.firstName",
                        Generalist.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Generalist save(Generalist generalist) {
        if (generalist.getId() == null) {
            entityManager.persist(generalist);
            return generalist;
        }
        return entityManager.merge(generalist);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Generalist managed = entityManager.find(Generalist.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Generalist> findByEmail(String email) {
        return entityManager.createQuery(
                        "SELECT g FROM Generalist g WHERE LOWER(g.email) = :email",
                        Generalist.class)
                .setParameter("email", email.toLowerCase())
                .getResultStream()
                .findFirst();
    }
}
