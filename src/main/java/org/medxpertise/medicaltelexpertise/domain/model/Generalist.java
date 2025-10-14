package org.medxpertise.medicaltelexpertise.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * @deprecated Use Doctor with doctorType = DoctorType.GENERALIST instead.
 * This class is maintained temporarily for backward compatibility.
 */
@Deprecated
@Entity
@DiscriminatorValue("GENERALIST")
public class Generalist extends Doctor {

    public void createConsultation() {
    }

    public void requestExpertise() {
    }
}
