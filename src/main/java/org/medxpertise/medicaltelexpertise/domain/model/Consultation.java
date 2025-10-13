package org.medxpertise.medicaltelexpertise.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultations")
public class Consultation {

    public static final BigDecimal DEFAULT_BASE_COST = BigDecimal.valueOf(150.0);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "generalist_id", nullable = false)
    private Generalist generalist;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime closedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ConsultationStatus status = ConsultationStatus.PENDING;

    @NotBlank
    @Size(max = 255)
    private String motif;

    @Lob
    private String observations;

    @Lob
    private String clinicalExam;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal baseCost = DEFAULT_BASE_COST;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Acte> actes = new ArrayList<>();

    @OneToOne(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ExpertiseRequest expertiseRequest;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> prescriptions = new ArrayList<>();

    public Consultation() {
    }

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ConsultationStatus.PENDING;
        }
        if (baseCost == null) {
            baseCost = DEFAULT_BASE_COST;
        }
    }

    public void addActe(Acte acte) {
        acte.setConsultation(this);
        actes.add(acte);
    }

    public void addPrescription(Prescription prescription) {
        prescription.setConsultation(this);
        prescriptions.add(prescription);
    }

    public BigDecimal calculateTotalCost() {
        BigDecimal actesTotal = actes.stream()
                .map(Acte::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expertiseCost = BigDecimal.ZERO;
        if (expertiseRequest != null && expertiseRequest.getSpecialistAssigned() != null && expertiseRequest.getSpecialistAssigned().getProfile() != null) {
            expertiseCost = BigDecimal.valueOf(expertiseRequest.getSpecialistAssigned().getProfile().getTarif());
        }

        return baseCost.add(actesTotal).add(expertiseCost);
    }

    public void closeConsultation() {
        status = ConsultationStatus.COMPLETED;
        closedAt = LocalDateTime.now();
    }

    public void markWaitingExpertise() {
        status = ConsultationStatus.WAITING_SPECIALIST_OPINION;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Generalist getGeneralist() {
        return generalist;
    }

    public void setGeneralist(Generalist generalist) {
        this.generalist = generalist;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public ConsultationStatus getStatus() {
        return status;
    }

    public void setStatus(ConsultationStatus status) {
        this.status = status;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getClinicalExam() {
        return clinicalExam;
    }

    public void setClinicalExam(String clinicalExam) {
        this.clinicalExam = clinicalExam;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }

    public List<Acte> getActes() {
        return actes;
    }

    public void setActes(List<Acte> actes) {
        this.actes = actes;
    }

    public ExpertiseRequest getExpertiseRequest() {
        return expertiseRequest;
    }

    public void setExpertiseRequest(ExpertiseRequest expertiseRequest) {
        this.expertiseRequest = expertiseRequest;
        if (expertiseRequest != null) {
            expertiseRequest.setConsultation(this);
        }
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }
}
