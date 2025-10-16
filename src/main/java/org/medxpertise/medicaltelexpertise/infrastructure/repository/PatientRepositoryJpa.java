package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.persistence.EntityManager;
import org.medxpertise.medicaltelexpertise.domain.model.Patient;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Gender;
import org.medxpertise.medicaltelexpertise.domain.repository.PatientRepository;
import org.medxpertise.medicaltelexpertise.infrastructure.config.JpaUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientRepositoryJpa implements PatientRepository {

    private EntityManager getEntityManager() {
        return JpaUtil.getEntityManager();
    }

    @Override
    public Optional<Patient> findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(em.find(Patient.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Patient> findAll() {
        EntityManager em = getEntityManager();
        try {
            // Use a DTO projection to avoid loading the Nurse relationship
            List<Object[]> results = em.createQuery(
                "SELECT p.id, p.cin, p.firstName, p.lastName, p.birthDate, " +
                "p.gender, p.phone, p.address, p.socialSecurityNumber, p.mutuelle, " +
                "p.antecedents, p.allergies, p.currentTreatments, p.registeredAt " +
                "FROM Patient p ORDER BY p.lastName, p.firstName", Object[].class)
                .getResultList();

            // Map the results to Patient objects
            List<Patient> patients = new ArrayList<>();
            for (Object[] row : results) {
                Patient patient = new Patient();
                patient.setId((Long) row[0]);
                patient.setCin((String) row[1]);
                patient.setFirstName((String) row[2]);
                patient.setLastName((String) row[3]);
                patient.setBirthDate((LocalDate) row[4]);
                patient.setGender((Gender) row[5]);
                patient.setPhone((String) row[6]);
                patient.setAddress((String) row[7]);
                patient.setSocialSecurityNumber((String) row[8]);
                patient.setMutuelle((String) row[9]);
                patient.setAntecedents((String) row[10]);
                patient.setAllergies((String) row[11]);
                patient.setCurrentTreatments((String) row[12]);
                patient.setRegisteredAt((LocalDateTime) row[13]);
                patients.add(patient);
            }
            return patients;
        } finally {
            em.close();
        }
    }

    @Override
    public Patient save(Patient patient) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Patient result;
            if (patient.getId() == null) {
                em.persist(patient);
                result = patient;
            } else {
                result = em.merge(patient);
            }
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Patient managed = em.find(Patient.class, id);
            if (managed != null) {
                em.remove(managed);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Patient> findByCin(String cin) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Patient p WHERE p.cin = :cin", Patient.class)
                    .setParameter("cin", cin)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Patient> findBySocialSecurityNumber(String ssn) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Patient p WHERE p.socialSecurityNumber = :ssn", Patient.class)
                    .setParameter("ssn", ssn)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Patient> findByLastNameLike(String lastNamePrefix) {
        EntityManager em = getEntityManager();
        try {
            String prefix = lastNamePrefix == null ? "" : lastNamePrefix.toLowerCase();
            return em.createQuery(
                            "SELECT p FROM Patient p " +
                                    "WHERE LOWER(p.lastName) LIKE :prefix " +
                                    "ORDER BY p.lastName, p.firstName",
                            Patient.class)
                    .setParameter("prefix", prefix + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Patient> findRegisteredOn(LocalDate date) {
        EntityManager em = getEntityManager();
        try {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = LocalDateTime.of(date.plusDays(1), LocalTime.MIDNIGHT);

            return em.createQuery(
                            "SELECT p FROM Patient p " +
                                    "WHERE p.registeredAt >= :start AND p.registeredAt < :end " +
                                    "ORDER BY p.registeredAt",
                            Patient.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                .getResultList();
    } finally {
            em.close();
        }
    }
}
