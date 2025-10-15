package org.medxpertise.medicaltelexpertise.domain.model;

import jakarta.persistence.*;

/**
 * @deprecated Use Doctor with doctorType = DoctorType.SPECIALIST instead.
 * This class is maintained temporarily for backward compatibility.
 */

@Deprecated
@Entity
@DiscriminatorValue("SPECIALIST")
public class Specialist extends Doctor {

    @OneToOne(mappedBy = "specialist", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private SpecialistProfile profile;

    public SpecialistProfile getProfile() {
        return profile;
    }

    public void setProfile(SpecialistProfile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setSpecialist(this);
        }
    }

    public void configureAvailability() {
    }

    public void respondToExpertise() {
    }
}
