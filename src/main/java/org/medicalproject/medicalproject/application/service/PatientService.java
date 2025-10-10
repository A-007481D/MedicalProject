package org.medicalproject.medicalproject.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.medicalproject.medicalproject.application.service.exception.BusinessRuleException;
import org.medicalproject.medicalproject.application.service.exception.ResourceNotFoundException;
import org.medicalproject.medicalproject.domain.model.AuditLog;
import org.medicalproject.medicalproject.domain.model.Nurse;
import org.medicalproject.medicalproject.domain.model.Patient;
import org.medicalproject.medicalproject.domain.model.QueueEntry;
import org.medicalproject.medicalproject.domain.model.VitalSign;
import org.medicalproject.medicalproject.domain.repository.AuditLogRepository;
import org.medicalproject.medicalproject.domain.repository.NurseRepository;
import org.medicalproject.medicalproject.domain.repository.PatientRepository;
import org.medicalproject.medicalproject.domain.repository.QueueEntryRepository;
import org.medicalproject.medicalproject.domain.repository.VitalSignRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PatientService {

    private final PatientRepository patientRepository;
    private final NurseRepository nurseRepository;
    private final VitalSignRepository vitalSignRepository;
    private final QueueEntryRepository queueEntryRepository;
    private final AuditLogRepository auditLogRepository;

    @Inject
    public PatientService(PatientRepository patientRepository,
                          NurseRepository nurseRepository,
                          VitalSignRepository vitalSignRepository,
                          QueueEntryRepository queueEntryRepository,
                          AuditLogRepository auditLogRepository) {
        this.patientRepository = patientRepository;
        this.nurseRepository = nurseRepository;
        this.vitalSignRepository = vitalSignRepository;
        this.queueEntryRepository = queueEntryRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public Patient registerPatient(Patient patient, Long nurseId) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient must not be null");
        }

        patient.getCin();
        patient.getSocialSecurityNumber();

        patient.getCin();
        if (patient.getCin() != null) {
            patientRepository.findByCin(patient.getCin())
                    .ifPresent(existing -> {
                        throw new BusinessRuleException("A patient with CIN " + patient.getCin() + " already exists");
                    });
        }
        if (patient.getSocialSecurityNumber() != null) {
            patientRepository.findBySocialSecurityNumber(patient.getSocialSecurityNumber())
                    .ifPresent(existing -> {
                        throw new BusinessRuleException("A patient with SSN " + patient.getSocialSecurityNumber() + " already exists");
                    });
        }

        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new ResourceNotFoundException("Nurse not found with id " + nurseId));

        patient.setCreatedBy(nurse);
        Patient saved = patientRepository.save(patient);

        auditLogRepository.save(new AuditLog("PATIENT_REGISTERED",
                "Patient registered with id " + saved.getId(), nurse));

        return saved;
    }

    @Transactional
    public VitalSign addVitalSign(Long patientId, VitalSign vitalSign, Long nurseId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + patientId));

        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new ResourceNotFoundException("Nurse not found with id " + nurseId));

        vitalSign.setPatient(patient);
        vitalSign.setRecordedBy(nurse);

        VitalSign saved = vitalSignRepository.save(vitalSign);

        auditLogRepository.save(new AuditLog("VITAL_SIGN_RECORDED",
                "Vitals recorded for patient " + patientId, nurse));

        return saved;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Patient getPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + patientId));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Patient> searchPatientsByLastNamePrefix(String lastNamePrefix) {
        return patientRepository.findByLastNameLike(lastNamePrefix == null ? "" : lastNamePrefix);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Patient> getPatientsRegisteredOn(LocalDate date) {
        return patientRepository.findRegisteredOn(date);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<VitalSign> getLatestVitals(Long patientId) {
        return vitalSignRepository.findLatestByPatient(patientId);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<QueueEntry> getQueueHistory(Long patientId) {
        return queueEntryRepository.findByPatientId(patientId);
    }
}
