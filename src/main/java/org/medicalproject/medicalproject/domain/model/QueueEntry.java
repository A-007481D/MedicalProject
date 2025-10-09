package org.medicalproject.medicalproject.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.medicalproject.medicalproject.domain.model.enums.QueueStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "queue_entries")
public class QueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QueueStatus status = QueueStatus.WAITING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private Nurse createdBy;

    @PrePersist
    private void onCreate() {
        if (arrivalTime == null) {
            arrivalTime = LocalDateTime.now();
        }
        if (status == null) {
            status = QueueStatus.WAITING;
        }
    }

    public void enqueue() {
        status = QueueStatus.WAITING;
        arrivalTime = LocalDateTime.now();
    }

    public void markInConsultation() {
        status = QueueStatus.IN_CONSULT;
    }

    public void dequeue() {
        status = QueueStatus.DONE;
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

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }

    public Nurse getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Nurse createdBy) {
        this.createdBy = createdBy;
    }
}
