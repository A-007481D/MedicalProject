package org.medicalproject.medicalproject.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.medicalproject.medicalproject.domain.model.User;
import org.medicalproject.medicalproject.domain.model.enums.Role;
import org.medicalproject.medicalproject.domain.repository.UserRepository;

import java.util.Optional;

@ApplicationScoped
public class AuthenticationService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public User registerUser(String username, String firstName, String lastName, String email, String password, Role role) {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        // Hash password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Create and save user
        User user = new User(username, firstName, lastName, hashedPassword, email, role);
        userRepository.save(user);
        return user;
    }

    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                user.login();
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
