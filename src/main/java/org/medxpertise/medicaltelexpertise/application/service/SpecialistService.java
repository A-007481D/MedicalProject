package org.medxpertise.medicaltelexpertise.application.service;

import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.SpecialistProfile;
import org.medxpertise.medicaltelexpertise.domain.model.Timeslot;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;
import org.medxpertise.medicaltelexpertise.domain.repository.SpecialistRepository;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.SpecialistRepositoryJpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SpecialistService {

    private static final Logger logger = Logger.getLogger(SpecialistService.class.getName());
    private final SpecialistRepository specialistRepository = new SpecialistRepositoryJpa();

    public SpecialistProfile getSpecialistProfile(Long specialistId) {
        logger.info("Getting specialist profile for specialist ID: " + specialistId);
        return specialistRepository.findProfileBySpecialistId(specialistId);
    }

    public SpecialistProfile createOrUpdateProfile(SpecialistProfile profile) {
        logger.info("Creating or updating specialist profile for specialist ID: " + profile.getSpecialist().getId());
        return specialistRepository.saveProfile(profile);
    }

    public List<Timeslot> getTodayTimeslots(Long specialistId) {
        logger.info("Getting today's timeslots for specialist ID: " + specialistId);
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        return specialistRepository.findTimeslotsBySpecialistAndDateRange(specialistId, startOfDay, endOfDay);
    }

    public List<Timeslot> getAllTimeslots(Long specialistId) {
        logger.info("Getting all timeslots for specialist ID: " + specialistId);
        return specialistRepository.findTimeslotsBySpecialistId(specialistId);
    }

    public Timeslot createTimeslot(Timeslot timeslot) {
        logger.info("Creating timeslot for specialist ID: " + timeslot.getProfile().getSpecialist().getId());
        return specialistRepository.saveTimeslot(timeslot);
    }

    public void deleteTimeslot(Long timeslotId) {
        logger.info("Deleting timeslot ID: " + timeslotId);
        specialistRepository.deleteTimeslotById(timeslotId);
    }

    public List<Consultation> getRecentConsultations(Long specialistId, int limit) {
        logger.info("Getting recent consultations for specialist ID: " + specialistId + ", limit: " + limit);
        return specialistRepository.findRecentConsultationsBySpecialist(specialistId, limit);
    }

    public List<Consultation> getConsultationsByStatus(Long specialistId, ConsultationStatus status) {
        logger.info("Getting consultations by status for specialist ID: " + specialistId + ", status: " + status);
        return specialistRepository.findConsultationsBySpecialistAndStatus(specialistId, status);
    }

    public Doctor getSpecialistById(Long specialistId) {
        logger.info("Getting specialist by ID: " + specialistId);
        return specialistRepository.findById(specialistId).orElse(null);
    }

    public boolean isSpecialistAvailable(Long specialistId, LocalDateTime requestedTime) {
        logger.info("Checking availability for specialist ID: " + specialistId + " at time: " + requestedTime);
        return specialistRepository.isSpecialistAvailable(specialistId, requestedTime);
    }

    public List<Doctor> getAllSpecialists() {
        logger.info("Getting all specialists");
        return specialistRepository.findAllSpecialists();
    }

    public List<Doctor> getSpecialistsBySpecialty(String specialty) {
        logger.info("Getting specialists by specialty: " + specialty);
        return specialistRepository.findSpecialistsBySpecialty(specialty);
    }
}
