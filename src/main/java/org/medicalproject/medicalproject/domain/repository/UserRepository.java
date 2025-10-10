package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
