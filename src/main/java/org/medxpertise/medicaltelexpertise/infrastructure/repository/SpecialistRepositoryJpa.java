package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.SpecialistProfile;
import org.medxpertise.medicaltelexpertise.domain.model.Timeslot;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.repository.SpecialistRepository;
import org.medxpertise.medicaltelexpertise.infrastructure.config.JpaUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SpecialistRepositoryJpa implements SpecialistRepository {

    private static final Logger logger = Logger.getLogger(SpecialistRepositoryJpa.class.getName());

    @Override
    public Optional<Doctor> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Doctor.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Doctor> findAllSpecialists() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT d FROM Doctor d WHERE d.doctorType = :doctorType ORDER BY d.firstName, d.lastName",
                            Doctor.class)
                    .setParameter("doctorType", DoctorType.SPECIALIST)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Doctor> findSpecialistsBySpecialty(String specialty) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT d FROM Doctor d WHERE d.doctorType = :doctorType AND d.specialty = :specialty ORDER BY d.firstName, d.lastName",
                            Doctor.class)
                    .setParameter("doctorType", DoctorType.SPECIALIST)
                    .setParameter("specialty", specialty)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public SpecialistProfile findProfileBySpecialistId(Long specialistId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT sp FROM SpecialistProfile sp WHERE sp.specialist.id = :specialistId",
                            SpecialistProfile.class)
                    .setParameter("specialistId", specialistId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public SpecialistProfile saveProfile(SpecialistProfile profile) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (profile.getId() == null) {
                em.persist(profile);
            } else {
                profile = em.merge(profile);
            }
            em.getTransaction().commit();
            return profile;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Timeslot> findTimeslotsBySpecialistId(Long specialistId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM Timeslot t WHERE t.profile.specialist.id = :specialistId ORDER BY t.startTime",
                            Timeslot.class)
                    .setParameter("specialistId", specialistId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Timeslot> findTimeslotsBySpecialistAndDateRange(Long specialistId, LocalDateTime start, LocalDateTime end) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM Timeslot t WHERE t.profile.specialist.id = :specialistId " +
                            "AND t.startTime >= :start AND t.endTime <= :end ORDER BY t.startTime",
                            Timeslot.class)
                    .setParameter("specialistId", specialistId)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Timeslot saveTimeslot(Timeslot timeslot) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (timeslot.getId() == null) {
                em.persist(timeslot);
            } else {
                timeslot = em.merge(timeslot);
            }
            em.getTransaction().commit();
            return timeslot;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteTimeslotById(Long timeslotId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Timeslot managed = em.find(Timeslot.class, timeslotId);
            if (managed != null) {
                em.remove(managed);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Consultation> findRecentConsultationsBySpecialist(Long specialistId, int limit) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Consultation c WHERE c.expertiseRequest.specialist.id = :specialistId " +
                            "ORDER BY c.createdAt DESC",
                            Consultation.class)
                    .setParameter("specialistId", specialistId)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Consultation> findConsultationsBySpecialistAndStatus(Long specialistId, ConsultationStatus status) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Consultation c WHERE c.expertiseRequest.specialist.id = :specialistId " +
                            "AND c.status = :status ORDER BY c.createdAt DESC",
                            Consultation.class)
                    .setParameter("specialistId", specialistId)
                    .setParameter("status", status)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean isSpecialistAvailable(Long specialistId, LocalDateTime requestedTime) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(t) FROM Timeslot t WHERE t.profile.specialist.id = :specialistId " +
                            "AND t.startTime <= :requestedTime AND t.endTime > :requestedTime " +
                            "AND t.isAvailable = true",
                            Long.class)
                    .setParameter("specialistId", specialistId)
                    .setParameter("requestedTime", requestedTime)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Timeslot> findAvailableTimeslot(Long specialistId, LocalDateTime requestedTime) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.createQuery(
                            "SELECT t FROM Timeslot t WHERE t.profile.specialist.id = :specialistId " +
                            "AND t.startTime <= :requestedTime AND t.endTime > :requestedTime " +
                            "AND t.isAvailable = true",
                            Timeslot.class)
                    .setParameter("specialistId", specialistId)
                    .setParameter("requestedTime", requestedTime)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
