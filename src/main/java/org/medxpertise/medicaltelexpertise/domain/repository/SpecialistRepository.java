package org.medxpertise.medicaltelexpertise.domain.repository;

import jakarta.enterprise.inject.Default;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.SpecialistProfile;
import org.medxpertise.medicaltelexpertise.domain.model.Timeslot;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Default
public interface SpecialistRepository {

    Optional<Doctor> findById(Long id);

    List<Doctor> findAllSpecialists();

    List<Doctor> findSpecialistsBySpecialty(String specialty);

    SpecialistProfile findProfileBySpecialistId(Long specialistId);

    SpecialistProfile saveProfile(SpecialistProfile profile);

    List<Timeslot> findTimeslotsBySpecialistId(Long specialistId);

    List<Timeslot> findTimeslotsBySpecialistAndDateRange(Long specialistId, LocalDateTime start, LocalDateTime end);

    Timeslot saveTimeslot(Timeslot timeslot);

    void deleteTimeslotById(Long timeslotId);

    List<Consultation> findRecentConsultationsBySpecialist(Long specialistId, int limit);

    List<Consultation> findConsultationsBySpecialistAndStatus(Long specialistId, ConsultationStatus status);

    boolean isSpecialistAvailable(Long specialistId, LocalDateTime requestedTime);

    Optional<Timeslot> findAvailableTimeslot(Long specialistId, LocalDateTime requestedTime);
}
