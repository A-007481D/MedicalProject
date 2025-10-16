package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.repository.UserRepository;
import org.medxpertise.medicaltelexpertise.infrastructure.config.JpaUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserRepositoryJpa implements UserRepository {

    @Override
    public Optional<User> findByUsername(String username) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.auditLogs WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.auditLogs WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public User save(User user) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Check if the entity exists first
            User existing = em.find(User.class, user.getId());
            User saved;
            
            if (existing != null) {
                // If it exists, update its fields
                copyNonNullProperties(user, existing, "id", "version");
                saved = em.merge(existing);
            } else {
                // If it doesn't exist, persist it
                em.persist(user);
                saved = user;
            }
            
            em.getTransaction().commit();
            return saved;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to save user", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Copies non-null properties from source to target object
     * @param source The source object to copy from
     * @param target The target object to copy to
     * @param ignoreProperties Properties to ignore during copy
     */
    private void copyNonNullProperties(Object source, Object target, String... ignoreProperties) {
        try {
            Class<?> clazz = source.getClass();
            List<String> ignoreList = Arrays.asList(ignoreProperties);
            
            // Copy fields from source to target
            for (Field field : clazz.getDeclaredFields()) {
                // Skip ignored fields
                if (ignoreList.contains(field.getName())) {
                    continue;
                }
                
                field.setAccessible(true);
                Object value = field.get(source);
                if (value != null) {
                    field.set(target, value);
                }
            }
            
            // Handle superclass fields if they exist
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !superClass.equals(Object.class)) {
                for (Field field : superClass.getDeclaredFields()) {
                    if (ignoreList.contains(field.getName())) {
                        continue;
                    }
                    field.setAccessible(true);
                    Object value = field.get(source);
                    if (value != null) {
                        field.set(target, value);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to copy properties", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            User managed = em.find(User.class, id);
            if (managed != null) {
                em.remove(managed);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(User.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u ORDER BY u.lastName, u.firstName", User.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
