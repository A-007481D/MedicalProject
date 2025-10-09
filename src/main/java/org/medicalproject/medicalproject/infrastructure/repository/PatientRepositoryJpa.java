package org.medicalproject.medicalproject.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.medicalproject.medicalproject.domain.model.Patient;
import org.medicalproject.medicalproject.domain.repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PatientRepositoryJpa implements PatientRepository {

    @PersistenceContext(unitName = "medicalPU")
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Patient> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Patient.class, id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Patient> findAll() {
        return entityManager.createQuery(
                        "SELECT p FROM Patient p ORDER BY p.lastName, p.firstName", Patient.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Patient save(Patient patient) {
        if (patient.getId() == null) {
            entityManager.persist(patient);
            return patient;
        }
        return entityManager.merge(patient);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Patient managed = entityManager.find(Patient.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Patient> findByCin(String cin) {
        return entityManager.createQuery(
                        "SELECT p FROM Patient p WHERE p.cin = :cin", Patient.class)
                .setParameter("cin", cin)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Patient> findBySocialSecurityNumber(String ssn) {
        return entityManager.createQuery(
                        "SELECT p FROM Patient p WHERE p.socialSecurityNumber = :ssn", Patient.class)
                .setParameter("ssn", ssn)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Patient> findByLastNameLike(String lastNamePrefix) {
        String prefix = lastNamePrefix == null ? "" : lastNamePrefix.toLowerCase();
        return entityManager.createQuery(
                        "SELECT p FROM Patient p " +
                                "WHERE LOWER(p.lastName) LIKE :prefix " +
                                "ORDER BY p.lastName, p.firstName",
                        Patient.class)
                .setParameter("prefix", prefix + "%")
                .getResultList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Patient> findRegisteredOn(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = LocalDateTime.of(date.plusDays(1), LocalTime.MIDNIGHT);

        return entityManager.createQuery(
                        "SELECT p FROM Patient p " +
                                "WHERE p.registeredAt >= :start AND p.registeredAt < :end " +
                                "ORDER BY p.registeredAt",
                        Patient.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
