package org.medxpertise.medicaltelexpertise.domain.model;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BASE")
public class BaseUser extends User {
    public BaseUser() {
        super();
    }
}
