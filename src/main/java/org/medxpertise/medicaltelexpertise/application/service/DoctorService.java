package org.medxpertise.medicaltelexpertise.application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Specialty;

import java.util.List;

public class DoctorService {

    private final EntityManagerFactory emf;

    public DoctorService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Doctor> findSpecialistsBySpecialty(Specialty specialty) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT d FROM Doctor d WHERE d.specialty = :specialty AND d.doctorType = :doctorType";
            
            TypedQuery<Doctor> query = em.createQuery(jpql, Doctor.class)
                .setParameter("specialty", specialty)
                .setParameter("doctorType", DoctorType.SPECIALIST);
                
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Doctor findDoctorWithProfileAndTimeslots(Long doctorId) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT d FROM Doctor d " +
                         "LEFT JOIN FETCH d.profile p " +
                         "LEFT JOIN FETCH p.timeslots t " +
                         "WHERE d.id = :doctorId";
            
            List<Doctor> results = em.createQuery(jpql, Doctor.class)
                .setParameter("doctorId", doctorId)
                .getResultList();
                
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
}
