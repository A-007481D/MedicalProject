package org.medxpertise.medicaltelexpertise.application.service;

import org.medxpertise.medicaltelexpertise.application.service.exception.BusinessRuleException;
import org.medxpertise.medicaltelexpertise.domain.model.Nurse;
import org.medxpertise.medicaltelexpertise.domain.model.Patient;
import org.medxpertise.medicaltelexpertise.domain.model.VitalSign;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Gender;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.NurseRepositoryJpa;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.PatientRepositoryJpa;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.VitalSignRepositoryJpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PatientService {
    
    private final PatientRepositoryJpa patientRepository;
    private final VitalSignRepositoryJpa vitalSignRepository;
    private final NurseRepositoryJpa nurseRepository;
    
    public PatientService() {
        this.patientRepository = new PatientRepositoryJpa();
        this.vitalSignRepository = new VitalSignRepositoryJpa();
        this.nurseRepository = new NurseRepositoryJpa();
    }
    
    /**
     * Search for patient by CIN or Social Security Number
     */
    public Optional<Patient> searchPatient(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return Optional.empty();
        }
        
        identifier = identifier.trim();
        
        // Try to find by CIN first
        Optional<Patient> patient = patientRepository.findByCin(identifier);
        if (patient.isPresent()) {
            return patient;
        }
        
        // Then try by SSN
        return patientRepository.findBySocialSecurityNumber(identifier);
    }
    
    /**
     * Register a new patient with all information
     */
    public Patient registerNewPatient(
            String cin,
            String firstName,
            String lastName,
            LocalDate birthDate,
            Gender gender,
            String phone,
            String address,
            String socialSecurityNumber,
            String mutuelle,
            String antecedents,
            String allergies,
            String currentTreatments,
            Long nurseId) {
        
        // Validate required fields
        if (cin == null || cin.trim().isEmpty()) {
            throw new BusinessRuleException("CIN is required");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new BusinessRuleException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new BusinessRuleException("Last name is required");
        }
        
        // Check if patient already exists
        if (patientRepository.findByCin(cin).isPresent()) {
            throw new BusinessRuleException("Patient with this CIN already exists");
        }
        
        if (socialSecurityNumber != null && !socialSecurityNumber.trim().isEmpty()) {
            if (patientRepository.findBySocialSecurityNumber(socialSecurityNumber).isPresent()) {
                throw new BusinessRuleException("Patient with this SSN already exists");
            }
        }
        
        // Get the nurse
        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new BusinessRuleException("Nurse not found"));
        
        // Create patient
        Patient patient = new Patient();
        patient.setCin(cin.trim());
        patient.setFirstName(firstName.trim());
        patient.setLastName(lastName.trim());
        patient.setBirthDate(birthDate);
        patient.setGender(gender);
        patient.setPhone(phone != null ? phone.trim() : null);
        patient.setAddress(address != null ? address.trim() : null);
        patient.setSocialSecurityNumber(socialSecurityNumber != null ? socialSecurityNumber.trim() : null);
        patient.setMutuelle(mutuelle != null ? mutuelle.trim() : null);
        patient.setAntecedents(antecedents);
        patient.setAllergies(allergies);
        patient.setCurrentTreatments(currentTreatments);
        patient.setCreatedBy(nurse);
        patient.setRegisteredAt(LocalDateTime.now());
        
        return patientRepository.save(patient);
    }
    
    /**
     * Add vital signs to a patient
     */
    public VitalSign addVitalSigns(
            Long patientId,
            Double temperature,
            Integer pulse,
            String bloodPressure,
            Integer respiratoryRate,
            Double weight,
            Double height,
            Long nurseId) {
        
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new BusinessRuleException("Patient not found"));
        
        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new BusinessRuleException("Nurse not found"));
        
        VitalSign vitalSign = new VitalSign();
        vitalSign.setPatient(patient);
        vitalSign.setRecordedBy(nurse);
        vitalSign.setTemperature(temperature);
        vitalSign.setPulse(pulse);
        vitalSign.setBloodPressure(bloodPressure);
        vitalSign.setRespiratoryRate(respiratoryRate);
        vitalSign.setWeight(weight);
        vitalSign.setHeight(height);
        vitalSign.setRecordedAt(LocalDateTime.now());
        
        return vitalSignRepository.save(vitalSign);
    }
    
    /**
     * Get all patients registered today, sorted by arrival time
     */
    public List<Patient> getTodayPatients() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        return patientRepository.findAll().stream()
                .filter(p -> p.getRegisteredAt().isAfter(startOfDay) && 
                           p.getRegisteredAt().isBefore(endOfDay))
                .sorted((p1, p2) -> p1.getRegisteredAt().compareTo(p2.getRegisteredAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get patient by ID
     */
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Patient not found"));
    }
    
    /**
     * Get all patients
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
}