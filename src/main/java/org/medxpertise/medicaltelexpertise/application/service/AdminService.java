package org.medxpertise.medicaltelexpertise.application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.*;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Specialty;
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

            // Use native queries to avoid version conflicts
            User user = em.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
            if (user == null) throw new IllegalArgumentException("User not found");

            Role oldRole = user.getRole();
            if (oldRole == newRole) return;

            // First, update the user role
            em.createNativeQuery("UPDATE users SET role = :newRole WHERE id = :userId")
              .setParameter("newRole", newRole.name())
              .setParameter("userId", userId)
              .executeUpdate();

            // Remove old role-specific data
            switch (oldRole) {
                case NURSE -> 
                    em.createNativeQuery("DELETE FROM nurse WHERE id = :id")
                      .setParameter("id", userId)
                      .executeUpdate();
                case GENERALIST, SPECIALIST -> 
                    em.createNativeQuery("DELETE FROM doctor WHERE id = :id")
                      .setParameter("id", userId)
                      .executeUpdate();
                case ADMIN -> 
                    em.createNativeQuery("DELETE FROM admin WHERE id = :id")
                      .setParameter("id", userId)
                      .executeUpdate();
                default -> {}
            }

            // Add new role-specific data
            switch (newRole) {
                case NURSE -> {
                    // Check if nurse record already exists
                    Long count = (Long) em.createNativeQuery("SELECT COUNT(*) FROM nurse WHERE id = :id")
                                       .setParameter("id", userId)
                                       .getSingleResult();
                    if (count == 0) {
                        em.createNativeQuery("INSERT INTO nurse (id) VALUES (:id)")
                          .setParameter("id", userId)
                          .executeUpdate();
                    }
                }
                case GENERALIST -> 
                    em.createNativeQuery("INSERT INTO doctor (id, doctor_type, specialty) VALUES (:id, 'GENERALIST', 'GENERAL_PRACTICE')")
                      .setParameter("id", userId)
                      .executeUpdate();
                case SPECIALIST -> 
                    em.createNativeQuery("INSERT INTO doctor (id, doctor_type, specialty) VALUES (:id, 'SPECIALIST', 'CARDIOLOGY')")
                      .setParameter("id", userId)
                      .executeUpdate();
                case ADMIN -> 
                    em.createNativeQuery("INSERT INTO admin (id) VALUES (:id)")
                      .setParameter("id", userId)
                      .executeUpdate();
                default -> {}
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Failed to change user role: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    private void updateUserRole(EntityManager em, User user, Role oldRole, Role newRole) {
        // Update the user's role
        user.setRole(newRole);
        em.merge(user);

        // Remove old subclass
        switch (oldRole) {
            case NURSE -> {
                Nurse nurse = em.find(Nurse.class, user.getId());
                if (nurse != null) em.remove(nurse);
            }
            case GENERALIST, SPECIALIST -> {
                Doctor doctor = em.find(Doctor.class, user.getId());
                if (doctor != null) em.remove(doctor);
            }
            case ADMIN -> {
                Admin admin = em.find(Admin.class, user.getId());
                if (admin != null) em.remove(admin);
            }
        }

        // Create or merge new subclass entity
        switch (newRole) {
            case NURSE -> {
                Nurse nurse = new Nurse();
                nurse.setId(user.getId());
                em.merge(nurse); // merge handles both new and existing entities
            }
            case GENERALIST, SPECIALIST -> {
                Doctor doctor = new Doctor();
                doctor.setId(user.getId());
                doctor.setDoctorType(newRole == Role.GENERALIST ? DoctorType.GENERALIST : DoctorType.SPECIALIST);
                doctor.setSpecialty(newRole == Role.GENERALIST ? Specialty.GENERAL_PRACTICE : Specialty.CARDIOLOGY);
                em.merge(doctor);
            }
            case ADMIN -> {
                Admin admin = new Admin();
                admin.setId(user.getId());
                em.merge(admin);
            }
        }
    }

    // helper method
    private void persistOrMerge(EntityManager em, Object entity) {
        Object managed = em.find(entity.getClass(), em.getEntityManagerFactory()
                .getPersistenceUnitUtil().getIdentifier(entity));
        if (managed == null) em.persist(entity);
        else em.merge(entity);
    }


//    private void updateUserRole(EntityManager em, User user, Role oldRole, Role newRole) {
//        if (oldRole == Role.NURSE) {
//            em.createNativeQuery("DELETE FROM nurse WHERE id = :id")
//                    .setParameter("id", user.getId())
//                    .executeUpdate();
//        } else if (oldRole == Role.GENERALIST || oldRole == Role.SPECIALIST) {
//            em.createNativeQuery("DELETE FROM doctor WHERE id = :id")
//                    .setParameter("id", user.getId())
//                    .executeUpdate();
//        } else if (oldRole == Role.ADMIN) {
//            em.createNativeQuery("DELETE FROM admin WHERE id = :id")
//                    .setParameter("id", user.getId())
//                    .executeUpdate();
//        }
//
//        user.setRole(newRole);
//        em.merge(user);
//
//        switch (newRole) {
//            case NURSE:
//                em.createNativeQuery("INSERT INTO nurse (id, phone) VALUES (:id, NULL)")
//                        .setParameter("id", user.getId())
//                        .executeUpdate();
//                break;
//
//            case GENERALIST:
//                em.createNativeQuery("INSERT INTO doctor (id, phone, specialty, doctor_type) VALUES (:id, NULL, 'GENERAL_PRACTICE', 'GENERALIST')")
//                        .setParameter("id", user.getId())
//                        .executeUpdate();
//                break;
//
//            case SPECIALIST:
//                em.createNativeQuery("INSERT INTO doctor (id, phone, specialty, doctor_type) VALUES (:id, NULL, 'CARDIOLOGY', 'SPECIALIST')")
//                        .setParameter("id", user.getId())
//                        .executeUpdate();
//                break;
//
//            case ADMIN:
//                em.createNativeQuery("INSERT INTO admin (id) VALUES (:id)")
//                        .setParameter("id", user.getId())
//                        .executeUpdate();
//                break;
//
//            case BASE:
//                break;
//        }
//    }

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