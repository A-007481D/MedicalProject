package org.medicalproject.medicalproject.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;

@Entity
@DiscriminatorValue("NURSE")
public class Nurse extends User {

    @Size(max = 20)
    private String phone;

    public Nurse() {
        super();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return (getFirstName() == null ? "" : getFirstName()) +
               (getLastName() == null ? "" : (getFirstName() == null ? "" : " ") + getLastName());
    }
}