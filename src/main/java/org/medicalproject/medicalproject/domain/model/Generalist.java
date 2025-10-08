package org.medicalproject.medicalproject.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GENERALIST")
public class Generalist extends Doctor {

    public void createConsultation() {
        // domain logic will be in services; placeholder for intent
    }

    public void requestExpertise() {
        // domain logic will be in services; placeholder for intent
    }
}
