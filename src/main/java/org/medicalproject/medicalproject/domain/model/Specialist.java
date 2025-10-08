package org.medicalproject.medicalproject.domain.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SPECIALIST")
public class Specialist extends Doctor {

    @OneToOne(mappedBy = "specialist", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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
