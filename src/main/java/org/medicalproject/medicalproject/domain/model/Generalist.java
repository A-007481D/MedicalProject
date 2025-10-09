package org.medicalproject.medicalproject.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GENERALIST")
public class Generalist extends Doctor {

    public void createConsultation() {
    }

    public void requestExpertise() {
    }
}
