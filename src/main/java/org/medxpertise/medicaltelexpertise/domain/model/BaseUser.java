package org.medxpertise.medicaltelexpertise.domain.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("BASE")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class BaseUser extends User {
    public BaseUser() {
        super();
    }
}
