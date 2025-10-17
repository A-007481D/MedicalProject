package org.medxpertise.medicaltelexpertise.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.medxpertise.medicaltelexpertise.domain.model.enums.QueueStatus;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "queue_entries")
public class QueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QueueStatus status = QueueStatus.WAITING;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_id")
    private Nurse createdBy;

    private Date displayArrivalTime;

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

    }

    public void markInConsultation() {
        status = QueueStatus.IN_PROGRESS;
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


    public void setDisplayArrivalTime(Date displayArrivalTime) {
        this.displayArrivalTime = displayArrivalTime;
    }

        public Date getDisplayArrivalTime() {
        return displayArrivalTime;
    }
}
