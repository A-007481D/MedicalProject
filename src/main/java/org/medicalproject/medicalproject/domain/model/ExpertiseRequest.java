package org.medicalproject.medicalproject.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.medicalproject.medicalproject.domain.model.enums.ExpertiseStatus;
import org.medicalproject.medicalproject.domain.model.enums.PriorityLevel;

import java.time.LocalDateTime;

@Entity
@Table(name = "expertise_requests")
public class ExpertiseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false, unique = true)
    private Consultation consultation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialist_id")
    private Specialist specialistAssigned;

    @NotBlank
    @Size(max = 100)
    private String specializationNeeded;

    @Lob
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PriorityLevel priority = PriorityLevel.NORMAL;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime scheduledSlotStart;

    private LocalDateTime scheduledSlotEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpertiseStatus status = ExpertiseStatus.PENDING;

    @Lob
    private String expertOpinion;

    @Lob
    private String recommendations;

    @PrePersist
    private void onCreate() {
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ExpertiseStatus.PENDING;
        }
        if (priority == null) {
            priority = PriorityLevel.NORMAL;
        }
    }

    public void assignSpecialist(Specialist specialist) {
        this.specialistAssigned = specialist;
        if (specialist != null && specialist.getProfile() != null) {
            if (scheduledSlotStart == null && scheduledSlotEnd == null && !specialist.getProfile().getTimeslots().isEmpty()) {
                scheduledSlotStart = specialist.getProfile().getTimeslots().get(0).getStart();
                scheduledSlotEnd = specialist.getProfile().getTimeslots().get(0).getEnd();
            }
        }
    }

    public void markAsDone(String opinion, String recommendations) {
        this.expertOpinion = opinion;
        this.recommendations = recommendations;
        this.status = ExpertiseStatus.DONE;
        this.scheduledSlotEnd = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public Specialist getSpecialistAssigned() {
        return specialistAssigned;
    }

    public void setSpecialistAssigned(Specialist specialistAssigned) {
        this.specialistAssigned = specialistAssigned;
    }

    public String getSpecializationNeeded() {
        return specializationNeeded;
    }

    public void setSpecializationNeeded(String specializationNeeded) {
        this.specializationNeeded = specializationNeeded;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getScheduledSlotStart() {
        return scheduledSlotStart;
    }

    public void setScheduledSlotStart(LocalDateTime scheduledSlotStart) {
        this.scheduledSlotStart = scheduledSlotStart;
    }

    public LocalDateTime getScheduledSlotEnd() {
        return scheduledSlotEnd;
    }

    public void setScheduledSlotEnd(LocalDateTime scheduledSlotEnd) {
        this.scheduledSlotEnd = scheduledSlotEnd;
    }

    public ExpertiseStatus getStatus() {
        return status;
    }

    public void setStatus(ExpertiseStatus status) {
        this.status = status;
    }

    public String getExpertOpinion() {
        return expertOpinion;
    }

    public void setExpertOpinion(String expertOpinion) {
        this.expertOpinion = expertOpinion;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
}
