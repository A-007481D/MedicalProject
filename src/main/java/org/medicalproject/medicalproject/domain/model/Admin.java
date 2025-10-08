package org.medicalproject.medicalproject.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.medicalproject.medicalproject.domain.model.enums.Role;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    public Admin() {
        super();
    }

    public Admin(String username, String firstName, String lastName, String passwordHash, String email) {
        super(username, firstName, lastName, passwordHash, email, Role.ADMIN);
    }

    public void manageStaff() {
        // Implementation for staff management logic
    }
}