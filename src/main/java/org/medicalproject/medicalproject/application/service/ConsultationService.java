package org.medicalproject.medicalproject.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.medicalproject.medicalproject.application.service.exception.BusinessRuleException;
import org.medicalproject.medicalproject.application.service.exception.ResourceNotFoundException;
import org.medicalproject.medicalproject.domain.model.Acte;
import org.medicalproject.medicalproject.domain.model.AuditLog;
import org.medicalproject.medicalproject.domain.model.Consultation;
import org.medicalproject.medicalproject.domain.model.Generalist;
import org.medicalproject.medicalproject.domain.model.Patient;
import org.medicalproject.medicalproject.domain.model.Prescription;
import org.medicalproject.medicalproject.domain.model.QueueEntry;
import org.medicalproject.medicalproject.domain.model.enums.ConsultationStatus;
import org.medicalproject.medicalproject.domain.model.enums.QueueStatus;
import org.medicalproject.medicalproject.domain.repository.ActeRepository;
import org.medicalproject.medicalproject.domain.repository.AuditLogRepository;
import org.medicalproject.medicalproject.domain.repository.ConsultationRepository;
import org.medicalproject.medicalproject.domain.repository.GeneralistRepository;
import org.medicalproject.medicalproject.domain.repository.PatientRepository;
import org.medicalproject.medicalproject.domain.repository.PrescriptionRepository;
import org.medicalproject.medicalproject.domain.repository.QueueEntryRepository;

import java.util.List;

@ApplicationScoped
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final GeneralistRepository generalistRepository;
    private final QueueEntryRepository queueEntryRepository;
    private final ActeRepository acteRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final AuditLogRepository auditLogRepository;

    @Inject
    public ConsultationService(ConsultationRepository consultationRepository,
                               PatientRepository patientRepository,
                               GeneralistRepository generalistRepository,
                               QueueEntryRepository queueEntryRepository,
                               ActeRepository acteRepository,
                               PrescriptionRepository prescriptionRepository,
                               AuditLogRepository auditLogRepository) {
        this.consultationRepository = consultationRepository;
        this.patientRepository = patientRepository;
        this.generalistRepository = generalistRepository;
        this.queueEntryRepository = queueEntryRepository;
        this.acteRepository = acteRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public Consultation startConsultation(Long patientId, Long generalistId, Long queueEntryId, String motif) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + patientId));
        Generalist generalist = generalistRepository.findById(generalistId)
                .orElseThrow(() -> new ResourceNotFoundException("Generalist not found with id " + generalistId));

        QueueEntry entry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found with id " + queueEntryId));

        if (!entry.getPatient().getId().equals(patientId)) {
            throw new BusinessRuleException("Queue entry does not belong to patient " + patientId);
        }
        if (entry.getStatus() != QueueStatus.IN_CONSULT && entry.getStatus() != QueueStatus.WAITING) {
            throw new BusinessRuleException("Queue entry must be waiting or in consult to start consultation");
        }

        entry.markInConsultation();
        queueEntryRepository.save(entry);

        Consultation consultation = new Consultation();
        consultation.setPatient(patient);
        consultation.setGeneralist(generalist);
        consultation.setMotif(motif);
        consultation.setStatus(ConsultationStatus.EN_COURS);

        Consultation saved = consultationRepository.save(consultation);
        auditLogRepository.save(new AuditLog("CONSULTATION_STARTED",
                "Consultation " + saved.getId() + " started for patient " + patientId, generalist));
        return saved;
    }

    @Transactional
    public Acte addActe(Long consultationId, Acte acte) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id " + consultationId));

        if (consultation.getStatus() != ConsultationStatus.EN_COURS) {
            throw new BusinessRuleException("Actes can only be added to consultations in progress");
        }

        acte.setConsultation(consultation);
        return acteRepository.save(acte);
    }

    @Transactional
    public Prescription addPrescription(Long consultationId, Prescription prescription) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id " + consultationId));

        if (consultation.getStatus() != ConsultationStatus.EN_COURS) {
            throw new BusinessRuleException("Prescriptions can only be added to consultations in progress");
        }

        prescription.setConsultation(consultation);
        return prescriptionRepository.save(prescription);
    }

    @Transactional
    public Consultation closeConsultation(Long consultationId, String observations, String clinicalExam) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id " + consultationId));

        if (consultation.getStatus() == ConsultationStatus.TERMINEE) {
            return consultation;
        }

        consultation.setObservations(observations);
        consultation.setClinicalExam(clinicalExam);
        consultation.closeConsultation();

        Consultation saved = consultationRepository.save(consultation);

        queueEntryRepository.findCurrentForPatient(consultation.getPatient().getId(), List.of(QueueStatus.IN_CONSULT))
                .ifPresent(entry -> {
                    entry.dequeue();
                    queueEntryRepository.save(entry);
                });

        auditLogRepository.save(new AuditLog("CONSULTATION_CLOSED",
                "Consultation " + saved.getId() + " closed", consultation.getGeneralist()));

        return saved;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Consultation getConsultation(Long consultationId) {
        return consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id " + consultationId));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Consultation> getConsultationsForPatient(Long patientId) {
        return consultationRepository.findByPatientId(patientId);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Consultation> getConsultationsForGeneralist(Long generalistId, ConsultationStatus status) {
        return consultationRepository.findByGeneralistIdAndStatus(generalistId, status);
    }
}
