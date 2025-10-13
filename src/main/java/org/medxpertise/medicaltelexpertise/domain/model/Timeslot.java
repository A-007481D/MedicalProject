package org.medxpertise.medicaltelexpertise.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.medxpertise.medicaltelexpertise.domain.model.enums.TimeslotStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "timeslots")
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime start;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TimeslotStatus status = TimeslotStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private SpecialistProfile profile;

    public void reserve() {
        if (status == TimeslotStatus.AVAILABLE) {
            status = TimeslotStatus.RESERVED;
        }
    }

    public void free() {
        if (status == TimeslotStatus.RESERVED) {
            status = TimeslotStatus.AVAILABLE;
        }
    }

    public void markPast() {
        status = TimeslotStatus.PAST;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public TimeslotStatus getStatus() {
        return status;
    }

    public void setStatus(TimeslotStatus status) {
        this.status = status;
    }

    public SpecialistProfile getProfile() {
        return profile;
    }

    public void setProfile(SpecialistProfile profile) {
        this.profile = profile;
    }
}
