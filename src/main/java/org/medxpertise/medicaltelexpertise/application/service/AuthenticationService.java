package org.medxpertise.medicaltelexpertise.application.service;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.application.service.exception.BusinessRuleException;
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
        Optional<User> userOpt = userRepository.findByUsername(login);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(login);
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPasswordHash()) && user.isActive()) {
                user.login();
                return userRepository.save(user);
            }
        }

        return null;
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
