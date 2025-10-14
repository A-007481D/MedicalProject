package org.medxpertise.medicaltelexpertise.application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.*;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;
import org.medxpertise.medicaltelexpertise.infrastructure.config.JpaUtil;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.NurseRepositoryJpa;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.UserRepositoryJpa;

import java.util.List;

public class AdminService {
    private final UserRepositoryJpa userRepository = new UserRepositoryJpa();
    private final NurseRepositoryJpa nurseRepository = new NurseRepositoryJpa();
    private final EntityManager em = JpaUtil.getEntityManager();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void changeUserRole(Long userId, Role newRole) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.id = :id", User.class)
                    .setParameter("id", userId)
                    .getSingleResult();

            Role oldRole = user.getRole();
            if (oldRole == newRole) {
                return;
            }

            updateUserRole(em, user, oldRole, newRole);

            em.getTransaction().commit();
        } catch (NoResultException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new IllegalArgumentException("User not found", e);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to change user role: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    private void updateUserRole(EntityManager em, User user, Role oldRole, Role newRole) {
        if (oldRole == Role.NURSE) {
            em.createNativeQuery("DELETE FROM nurse WHERE id = :id")
                    .setParameter("id", user.getId())
                    .executeUpdate();
        } else if (oldRole == Role.GENERALIST || oldRole == Role.SPECIALIST) {
            em.createNativeQuery("DELETE FROM doctor WHERE id = :id")
                    .setParameter("id", user.getId())
                    .executeUpdate();
        } else if (oldRole == Role.ADMIN) {
            em.createNativeQuery("DELETE FROM admin WHERE id = :id")
                    .setParameter("id", user.getId())
                    .executeUpdate();
        }

        user.setRole(newRole);
        em.merge(user);

        switch (newRole) {
            case NURSE:
                em.createNativeQuery("INSERT INTO nurse (id, phone) VALUES (:id, NULL)")
                        .setParameter("id", user.getId())
                        .executeUpdate();
                break;

            case GENERALIST:
                em.createNativeQuery("INSERT INTO doctor (id, phone, specialty, doctor_type) VALUES (:id, NULL, 'GENERAL_PRACTICE', 'GENERALIST')")
                        .setParameter("id", user.getId())
                        .executeUpdate();
                break;

            case SPECIALIST:
                em.createNativeQuery("INSERT INTO doctor (id, phone, specialty, doctor_type) VALUES (:id, NULL, 'CARDIOLOGY', 'SPECIALIST')")
                        .setParameter("id", user.getId())
                        .executeUpdate();
                break;

            case ADMIN:
                em.createNativeQuery("INSERT INTO admin (id) VALUES (:id)")
                        .setParameter("id", user.getId())
                        .executeUpdate();
                break;

            case BASE:
                break;
        }
    }

    private void copyUserToNurse(User source, Nurse target) {
        target.setUsername(source.getUsername());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setEmail(source.getEmail());
        target.setPasswordHash(source.getPasswordHash());
        target.setRole(Role.NURSE);
        target.setActive(source.isActive());
        target.setCreatedAt(source.getCreatedAt());
        target.setLastLoginAt(source.getLastLoginAt());
    }

    private void copyUserToDoctor(User source, Doctor target) {
        target.setUsername(source.getUsername());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setEmail(source.getEmail());
        target.setPasswordHash(source.getPasswordHash());
        target.setRole(source.getRole());
        target.setActive(source.isActive());
        target.setCreatedAt(source.getCreatedAt());
        target.setLastLoginAt(source.getLastLoginAt());
    }

}