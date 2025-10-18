package org.medxpertise.medicaltelexpertise.application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Specialty;

import java.util.ArrayList;
import java.util.List;

public class DoctorService {

    private final EntityManagerFactory emf;

    public DoctorService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @SuppressWarnings("unchecked")
    public List<Doctor> findSpecialistsBySpecialty(Specialty specialty) {
        EntityManager em = emf.createEntityManager();
        try {
            String sql = "SELECT d.id, u.first_name, u.last_name, u.email, d.specialty, d.phone " +
                       "FROM doctor d " +
                       "JOIN users u ON d.id = u.id " +
                       "WHERE d.specialty = ? " +
                       "AND d.doctor_type = 'SPECIALIST' " +
                       "AND u.role = 'SPECIALIST'";
            
            List<Object[]> results = em.createNativeQuery(sql)
                                    .setParameter(1, specialty.name())
                                    .getResultList();
            
            List<Doctor> doctors = new ArrayList<>();
            for (Object[] row : results) {
                Doctor doctor = new Doctor();
                doctor.setId(((Number)row[0]).longValue());
                doctor.setFirstName((String)row[1]);
                doctor.setLastName((String)row[2]);
                doctor.setEmail((String)row[3]);
                doctor.setSpecialty(Specialty.valueOf((String)row[4]));
                doctor.setPhone((String)row[5]);
                doctor.setDoctorType(DoctorType.SPECIALIST);
                doctors.add(doctor);
            }
            
            return doctors;
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
