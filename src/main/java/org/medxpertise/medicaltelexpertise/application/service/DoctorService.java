package org.medxpertise.medicaltelexpertise.application.service;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Specialty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.DayOfWeek;
import org.medxpertise.medicaltelexpertise.domain.model.SpecialistProfile;
import org.medxpertise.medicaltelexpertise.domain.model.Timeslot;
import org.medxpertise.medicaltelexpertise.domain.model.enums.TimeslotStatus;

public class DoctorService {

    private final EntityManagerFactory emf;

    public DoctorService(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public Doctor findDoctorById(Long doctorId) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT d FROM Doctor d WHERE d.id = :doctorId";
            TypedQuery<Doctor> query = em.createQuery(jpql, Doctor.class)
                .setParameter("doctorId", doctorId);
            try {
                return query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        } finally {
            em.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public SpecialistProfile getSpecialistProfile(Long doctorId) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT p FROM SpecialistProfile p " +
                        "LEFT JOIN FETCH p.timeslots t " +
                        "LEFT JOIN FETCH p.specialist s " +
                        "WHERE p.specialist.id = :doctorId";
            
            TypedQuery<SpecialistProfile> query = em.createQuery(jpql, SpecialistProfile.class)
                .setParameter("doctorId", doctorId);
            
            try {
                return query.getSingleResult();
            } catch (NoResultException e) {
                Doctor doctor = em.find(Doctor.class, doctorId);
                if (doctor != null) {
                    SpecialistProfile newProfile = new SpecialistProfile();
                    newProfile.setSpecialist(doctor);
                    newProfile.setTarif(0.0); 
                    newProfile.setTimeslots(new ArrayList<>());
                    
                    EntityTransaction tx = em.getTransaction();
                    try {
                        tx.begin();
                        em.persist(newProfile);
                        tx.commit();
                        return newProfile;
                    } catch (Exception ex) {
                        if (tx != null && tx.isActive()) {
                            tx.rollback();
                        }
                        throw new RuntimeException("Failed to create new specialist profile", ex);
                    }
                }
                return null;
            }
        } finally {
            em.close();
        }
    }
    
    @Transactional
    public void saveSpecialistProfile(SpecialistProfile profile) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (profile.getId() == null) {
                em.persist(profile);
            } else {
                em.merge(profile);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Failed to save specialist profile", e);
        } finally {
            em.close();
        }
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

    @SuppressWarnings("unchecked")
    public Doctor findDoctorWithProfileAndTimeslots(Long doctorId) {
        EntityManager em = emf.createEntityManager();
        try {
            // First, get the basic doctor info
            String sql = "SELECT d.id, u.first_name, u.last_name, u.email, d.specialty, d.phone, " +
                       "p.id as profile_id, p.tarif, " +
                       "t.id as timeslot_id, t.start_time, t.end_time, t.status " +
                       "FROM doctor d " +
                       "JOIN users u ON d.id = u.id " +
                       "LEFT JOIN specialist_profiles p ON d.id = p.specialist_id " +
                       "LEFT JOIN timeslots t ON p.id = t.profile_id " +
                       "WHERE d.id = ?";
            
            List<Object[]> results = em.createNativeQuery(sql)
                                    .setParameter(1, doctorId)
                                    .getResultList();
            
            if (results.isEmpty()) {
                return null;
            }
            
            // Create and populate the doctor
            Object[] firstRow = results.get(0);
            Doctor doctor = new Doctor();
            doctor.setId(((Number)firstRow[0]).longValue());
            doctor.setFirstName((String)firstRow[1]);
            doctor.setLastName((String)firstRow[2]);
            doctor.setEmail((String)firstRow[3]);
            doctor.setSpecialty(Specialty.valueOf((String)firstRow[4]));
            doctor.setPhone((String)firstRow[5]);
            
            // Create and set the profile if it exists
            if (firstRow[6] != null) {
                SpecialistProfile profile = new SpecialistProfile();
                profile.setId(((Number)firstRow[6]).longValue());
                profile.setTarif((Double)firstRow[7]);
                doctor.setProfile(profile);
                
                // Add timeslots if they exist
                List<Timeslot> timeSlots = new ArrayList<>();
                for (Object[] row : results) {
                    if (row[8] != null) { // timeslot_id
                        Timeslot slot = new Timeslot();
                        slot.setId(((Number)row[8]).longValue());
                        
                        // Convert java.sql.Timestamp to LocalDateTime
                        if (row[9] != null) {
                            slot.setStart(((java.sql.Timestamp)row[9]).toLocalDateTime());
                        }
                        if (row[10] != null) {
                            slot.setEnd(((java.sql.Timestamp)row[10]).toLocalDateTime());
                        }
                        if (row[11] != null) {
                            slot.setStatus(TimeslotStatus.valueOf((String)row[11]));
                        }
                        slot.setProfile(profile);
                        timeSlots.add(slot);
                    }
                }
                if (!timeSlots.isEmpty()) {
                    profile.setTimeslots(timeSlots);
                }
            }
            
            return doctor;
        } finally {
            em.close();
        }
    }
}
