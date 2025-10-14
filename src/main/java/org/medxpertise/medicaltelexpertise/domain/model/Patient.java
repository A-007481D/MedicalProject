    package org.medxpertise.medicaltelexpertise.domain.model;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Size;
    import org.medxpertise.medicaltelexpertise.domain.model.enums.Gender;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.*;

    @Entity
    @Table(name = "patients",
            uniqueConstraints = {
                    @UniqueConstraint(name = "uk_patients_cin", columnNames = "cin"),
                    @UniqueConstraint(name = "uk_patients_ssn", columnNames = "social_security_number")
            })
    public class Patient {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank
        @Size(max = 20)
        @Column(nullable = false, length = 20)
        private String cin;

        @NotBlank
        @Size(max = 100)
        @Column(nullable = false, length = 100)
        private String firstName;

        @NotBlank
        @Size(max = 100)
        @Column(nullable = false, length = 100)
        private String lastName;

        private LocalDate birthDate;

        @Enumerated(EnumType.STRING)
        private Gender gender;

        @Size(max = 20)
        private String phone;

        @Size(max = 255)
        private String address;

        @Size(max = 30)
        @Column(name = "social_security_number", length = 30)
        private String socialSecurityNumber;

        @Size(max = 100)
        private String mutuelle;

        @Lob
        private String antecedents;

        @Lob
        private String allergies;

        @Lob
        private String currentTreatments;

        @Column(nullable = false)
        private LocalDateTime registeredAt;

        @Transient
        private Date displayRegisteredAt;


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "created_by_id")
        private Nurse createdBy;

        @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.EAGER)
        private List<VitalSign> vitalSigns = new ArrayList<>();

        @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<QueueEntry> queueEntries = new ArrayList<>();

        @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Consultation> consultations = new ArrayList<>();


        public Patient() {
        }

        @PrePersist
        private void onCreate() {
            if (registeredAt == null) {
                registeredAt = LocalDateTime.now();
            }
        }

        public void addVitalSign(VitalSign vitalSign) {
            vitalSign.setPatient(this);
            this.vitalSigns.add(vitalSign);
        }

        public Optional<VitalSign> getLatestVitals() {
            return vitalSigns.stream()
                    .max(Comparator.comparing(VitalSign::getRecordedAt));
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCin() {
            return cin;
        }

        public void setCin(String cin) {
            this.cin = cin;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSocialSecurityNumber() {
            return socialSecurityNumber;
        }

        public void setSocialSecurityNumber(String socialSecurityNumber) {
            this.socialSecurityNumber = socialSecurityNumber;
        }

        public String getMutuelle() {
            return mutuelle;
        }

        public void setMutuelle(String mutuelle) {
            this.mutuelle = mutuelle;
        }

        public String getAntecedents() {
            return antecedents;
        }

        public void setAntecedents(String antecedents) {
            this.antecedents = antecedents;
        }

        public String getAllergies() {
            return allergies;
        }

        public void setAllergies(String allergies) {
            this.allergies = allergies;
        }

        public String getCurrentTreatments() {
            return currentTreatments;
        }

        public void setCurrentTreatments(String currentTreatments) {
            this.currentTreatments = currentTreatments;
        }

        public LocalDateTime getRegisteredAt() {
            return registeredAt;
        }

        public void setRegisteredAt(LocalDateTime registeredAt) {
            this.registeredAt = registeredAt;
        }

        public Nurse getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Nurse createdBy) {
            this.createdBy = createdBy;
        }

        public List<VitalSign> getVitalSigns() {
            return vitalSigns;
        }

        public void setVitalSigns(List<VitalSign> vitalSigns) {
            this.vitalSigns = vitalSigns;
        }

        public List<QueueEntry> getQueueEntries() {
            return queueEntries;
        }

        public void setQueueEntries(List<QueueEntry> queueEntries) {
            this.queueEntries = queueEntries;
        }

        public List<Consultation> getConsultations() {
            return consultations;
        }

        public void setConsultations(List<Consultation> consultations) {
            this.consultations = consultations;
        }


        public Date getDisplayRegisteredAt() {
            return displayRegisteredAt;
        }

        public void setDisplayRegisteredAt(Date displayRegisteredAt) {
            this.displayRegisteredAt = displayRegisteredAt;
        }

    }
