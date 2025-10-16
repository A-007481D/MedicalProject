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
    public User changeUserRole(Long userId, Role newRole) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // 1. Find and lock the user
            User user = em.createQuery(
                "SELECT u FROM User u WHERE u.id = :userId", User.class)
                .setParameter("userId", userId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getSingleResult();

            Role oldRole = user.getRole();
            if (oldRole == newRole) {
                em.getTransaction().rollback();
                return user;
            }

            try {
                // 2. Create new instance of the correct type
                User updatedUser = createUserOfType(newRole, user);
                
                // 3. Copy all fields except id and version
                copyUserProperties(user, updatedUser);
                updatedUser.setRole(newRole);
                
                // Role-specific initialization is handled in createUserOfType

                // 5. Merge and return the updated user
                User mergedUser = em.merge(updatedUser);
                em.getTransaction().commit();
                return mergedUser;
                
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Failed to update user role", e);
            }
        } catch (NoResultException e) {
            throw new IllegalArgumentException("User not found with id: " + userId, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to change user role", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Copies properties from source user to target user, excluding id and version fields
     */
    private void copyUserProperties(User source, User target) {
        target.setUsername(source.getUsername());
        target.setEmail(source.getEmail());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setPasswordHash(source.getPasswordHash());
        target.setActive(source.isActive());
        target.setCreatedAt(source.getCreatedAt());
        target.setLastLoginAt(source.getLastLoginAt());
        target.setPhone(source.getPhone());
        // Note: We don't copy id, version, or role here
    }

    private User createUserOfType(Role role, User source) {
        User newUser;
        switch (role) {
            case ADMIN:
                newUser = new Admin();
                break;
            case GENERALIST:
            case SPECIALIST:
                Doctor doctor = new Doctor();
                doctor.setDoctorType(role == Role.SPECIALIST ? 
                    DoctorType.SPECIALIST : DoctorType.GENERALIST);
                doctor.setSpecialty(role == Role.SPECIALIST ? 
                    Specialty.CARDIOLOGY : Specialty.GENERAL_PRACTICE);
                newUser = doctor;
                break;
            case NURSE:
                newUser = new Nurse();
                break;
            default:
                newUser = new BaseUser();
        }
        
        // Copy common fields
        newUser.setId(source.getId());
        newUser.setUsername(source.getUsername());
        newUser.setEmail(source.getEmail());
        newUser.setFirstName(source.getFirstName());
        newUser.setLastName(source.getLastName());
        newUser.setPasswordHash(source.getPasswordHash());
        newUser.setActive(source.isActive());
        newUser.setCreatedAt(source.getCreatedAt());
        newUser.setLastLoginAt(source.getLastLoginAt());
        newUser.setPhone(source.getPhone());
        newUser.setVersion(source.getVersion());
        
        return newUser;
    }

    @Deprecated
    private void updateUserRole(EntityManager em, User user, Role oldRole, Role newRole) {
        // This method is kept for backward compatibility but should not be used
        // Use changeUserRole() instead which has proper transaction management
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