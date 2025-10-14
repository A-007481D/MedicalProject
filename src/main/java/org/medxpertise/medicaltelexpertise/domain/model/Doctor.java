package org.medxpertise.medicaltelexpertise.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Specialty;

@Entity
@DiscriminatorValue("DOCTOR")
public class Doctor extends User {

    @Size(max = 20)
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "specialty", length = 50)
    private Specialty specialty;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "doctor_type", length = 20)
    private DoctorType doctorType;

    @OneToOne(mappedBy = "specialist", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SpecialistProfile profile;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public DoctorType getDoctorType() {
        return doctorType;
    }

    public void setDoctorType(DoctorType doctorType) {
        this.doctorType = doctorType;
    }

    public boolean isGeneralist() {
        return doctorType == DoctorType.GENERALIST;
    }

    public boolean isSpecialist() {
        return doctorType == DoctorType.SPECIALIST;
    }

    public SpecialistProfile getProfile() {
        return profile;
    }

    public void setProfile(SpecialistProfile profile) {
        this.profile = profile;
        if (profile != null && profile.getSpecialist() != this) {
            profile.setSpecialist(this);
        }
    }
}