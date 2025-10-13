package org.medxpertise.medicaltelexpertise.application.service;

import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.UserRepositoryJpa;

import java.util.List;
import java.util.Optional;

public class AdminService {

    private final UserRepositoryJpa userRepository = new UserRepositoryJpa();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void changeUserRole(Long userId, Role newRole) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(newRole);
            userRepository.save(user);
        }
    }
}