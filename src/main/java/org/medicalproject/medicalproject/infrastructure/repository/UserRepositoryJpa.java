package org.medicalproject.medicalproject.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medicalproject.medicalproject.domain.model.User;
import org.medicalproject.medicalproject.domain.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryJpa implements UserRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u ORDER BY u.lastName, u.firstName", User.class)
                .getResultList();
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        }
        return entityManager.merge(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User managed = entityManager.find(User.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<User> findByUsername(String username) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<User> findByEmail(String email) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }
}
