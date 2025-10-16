package org.medxpertise.medicaltelexpertise.application.service;

import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.application.service.exception.BusinessRuleException;
import org.medxpertise.medicaltelexpertise.infrastructure.config.JpaUtil;
import org.medxpertise.medicaltelexpertise.domain.model.AuditLog;
import org.medxpertise.medicaltelexpertise.domain.model.BaseUser;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.UserRepositoryJpa;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthenticationService {

    private final UserRepositoryJpa userRepository = new UserRepositoryJpa();

    public AuthenticationService() {}

    public User register(String firstName, String lastName, String email, String rawPassword, String confirmPassword) {
        if (!rawPassword.equals(confirmPassword)) {
            throw new BusinessRuleException("Passwords do not match");
        }

        String username = generateUsername(firstName, lastName);

        if (userRepository.findByEmail(email).isPresent() || userRepository.findByUsername(username).isPresent()) {
            throw new BusinessRuleException("Email or username already exists");
        }

        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        BaseUser newUser = new BaseUser();
        newUser.setUsername(username);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashed);
        newUser.setRole(Role.BASE);
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());

        return userRepository.save(newUser);
    }

    @Transactional
    public User authenticate(String login, String password) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // 1. First, find just the user ID and password hash using a projection
            Object[] userData = em.createQuery(
                "SELECT u.id, u.passwordHash, u.active FROM User u " +
                "WHERE u.username = :login OR u.email = :login", Object[].class)
                .setParameter("login", login)
                .getResultStream()
                .findFirst()
                .orElse(null);

            if (userData == null) {
                return null;
            }

            Long userId = (Long) userData[0];
            String passwordHash = (String) userData[1];
            boolean isActive = (Boolean) userData[2];

            // 2. Verify password and active status
            if (!BCrypt.checkpw(password, passwordHash) || !isActive) {
                return null;
            }

            // 3. Use native SQL for updates to avoid entity state issues
            em.getTransaction().begin();
            try {
                // Update last login time
                em.createNativeQuery(
                    "UPDATE users SET last_login_at = :now WHERE id = :userId")
                    .setParameter("now", LocalDateTime.now())
                    .setParameter("userId", userId)
                    .executeUpdate();

                // Create audit log
                em.createNativeQuery(
                    "INSERT INTO audit_logs (actionCode, actor_id, details, timestamp) " +
                    "VALUES (:actionCode, :actorId, :details, :timestamp)")
                    .setParameter("actionCode", "USER_LOGIN")
                    .setParameter("actorId", userId)
                    .setParameter("details", "User logged in")
                    .setParameter("timestamp", LocalDateTime.now())
                    .executeUpdate();

                em.getTransaction().commit();
                
                // Return the managed user entity
                return em.find(User.class, userId);
                
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Failed to update user login information", e);
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void logout(jakarta.servlet.http.HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    private String generateUsername(String firstName, String lastName) {
        String baseUsername = (firstName.charAt(0) + lastName).toLowerCase().replaceAll("[^a-z0-9]", "");
        String username = baseUsername;
        int suffix = 1;

        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + suffix;
            suffix++;
        }

        return username;
    }
}
