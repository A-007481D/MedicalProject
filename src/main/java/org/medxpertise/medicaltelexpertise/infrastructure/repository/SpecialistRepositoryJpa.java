package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.persistence.NoResultException;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.SpecialistProfile;
import org.medxpertise.medicaltelexpertise.domain.model.Timeslot;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.repository.SpecialistRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import org.medxpertise.medicaltelexpertise.infrastructure.config.qualifier.AppEntityManager;

@ApplicationScoped
@Transactional
public class SpecialistRepositoryJpa implements SpecialistRepository {

    private static final Logger logger = Logger.getLogger(SpecialistRepositoryJpa.class.getName());

    @Inject
    @AppEntityManager
    private EntityManager entityManager;
    
    // Default constructor for CDI
    public SpecialistRepositoryJpa() {
        logger.info("SpecialistRepositoryJpa initialized");
    }
    
    // For testing purposes
    public SpecialistRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
        logger.info("SpecialistRepositoryJpa initialized with custom EntityManager");
    }
    public Optional<Doctor> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Doctor.class, id));
    }

    @Override
    public List<Doctor> findAllSpecialists() {
        return entityManager.createQuery(
                        "SELECT d FROM Doctor d WHERE d.doctorType = :doctorType ORDER BY d.firstName, d.lastName",
                        Doctor.class)
                .setParameter("doctorType", DoctorType.SPECIALIST)
                .getResultList();
    }

    @Override
    public List<Doctor> findSpecialistsBySpecialty(String specialty) {
        return entityManager.createQuery(
                        "SELECT d FROM Doctor d WHERE d.doctorType = :doctorType AND d.specialty = :specialty ORDER BY d.firstName, d.lastName",
                        Doctor.class)
                .setParameter("doctorType", DoctorType.SPECIALIST)
                .setParameter("specialty", specialty)
                .getResultList();
    }

    @Override
    public SpecialistProfile findProfileBySpecialistId(Long specialistId) {
        try {
            return entityManager.createQuery(
                            "SELECT sp FROM SpecialistProfile sp WHERE sp.specialist.id = :specialistId",
                            SpecialistProfile.class)
                    .setParameter("specialistId", specialistId)
                    .getSingleResult();
        } catch (NoResultException e) {
        }
        return null;
    }
    
    
    

    @Override
    public SpecialistProfile saveProfile(SpecialistProfile profile) {
        if (profile.getId() == null) {
            entityManager.persist(profile);
        } else {
            profile = entityManager.merge(profile);
        }
        return profile;
    }

    @Override
    public List<Timeslot> findTimeslotsBySpecialistId(Long specialistId) {
        return entityManager.createQuery(
                        "SELECT t FROM Timeslot t WHERE t.profile.specialist.id = :specialistId AND t.start > CURRENT_TIMESTAMP ORDER BY t.start",
                        Timeslot.class)
                .setParameter("specialistId", specialistId)
                .getResultList();
    }

    @Override
    public List<Timeslot> findTimeslotsBySpecialistAndDateRange(Long specialistId, LocalDateTime start, LocalDateTime end) {
        return entityManager.createQuery(
                        "SELECT t FROM Timeslot t WHERE t.profile.specialist.id = :specialistId AND t.start BETWEEN :start AND :end ORDER BY t.start",
                        Timeslot.class)
                .setParameter("specialistId", specialistId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    public Timeslot saveTimeslot(Timeslot timeslot) {
        if (timeslot.getId() == null) {
            entityManager.persist(timeslot);
        } else {
            timeslot = entityManager.merge(timeslot);
        }
        return timeslot;
    }

    @Override
    public void deleteTimeslotById(Long timeslotId) {
        Timeslot managed = entityManager.find(Timeslot.class, timeslotId);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    public List<Consultation> findRecentConsultationsBySpecialist(Long specialistId, int limit) {
        return entityManager.createQuery(
                        "SELECT c FROM Consultation c WHERE c.generalist.id = :specialistId ORDER BY c.createdAt DESC",
                        Consultation.class)
                .setParameter("specialistId", specialistId)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<Consultation> findConsultationsBySpecialistAndStatus(Long specialistId, ConsultationStatus status) {
        return entityManager.createQuery(
                        "SELECT c FROM Consultation c WHERE c.generalist.id = :specialistId AND c.status = :status ORDER BY c.createdAt DESC",
                        Consultation.class)
                .setParameter("specialistId", specialistId)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public boolean isSpecialistAvailable(Long specialistId, LocalDateTime requestedTime) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(t) FROM Timeslot t WHERE t.doctor.id = :specialistId " +
                        "AND t.startTime <= :requestedTime AND t.endTime > :requestedTime " +
                        "AND t.isAvailable = true",
                        Long.class)
                .setParameter("specialistId", specialistId)
                .setParameter("requestedTime", requestedTime)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<Timeslot> findAvailableTimeslot(Long specialistId, LocalDateTime requestedTime) {
        try {
            return Optional.ofNullable(entityManager.createQuery(
                            "SELECT t FROM Timeslot t WHERE t.doctor.id = :specialistId " +
                            "AND t.startTime <= :requestedTime AND t.endTime > :requestedTime " +
                            "AND t.isAvailable = true",
                            Timeslot.class)
                    .setParameter("specialistId", specialistId)
                    .setParameter("requestedTime", requestedTime)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
