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
import org.medicalproject.medicalproject.domain.model.enums.QueueStatus;
import org.medicalproject.medicalproject.domain.repository.AuditLogRepository;
import org.medicalproject.medicalproject.domain.repository.NurseRepository;
import org.medicalproject.medicalproject.domain.repository.PatientRepository;
import org.medicalproject.medicalproject.domain.repository.QueueEntryRepository;

import java.util.List;

@ApplicationScoped
public class QueueService {

    private final QueueEntryRepository queueEntryRepository;
    private final PatientRepository patientRepository;
    private final NurseRepository nurseRepository;
    private final AuditLogRepository auditLogRepository;

    @Inject
    public QueueService(QueueEntryRepository queueEntryRepository,
                        PatientRepository patientRepository,
                        NurseRepository nurseRepository,
                        AuditLogRepository auditLogRepository) {
        this.queueEntryRepository = queueEntryRepository;
        this.patientRepository = patientRepository;
        this.nurseRepository = nurseRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public QueueEntry enqueuePatient(Long patientId, Long nurseId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + patientId));

        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new ResourceNotFoundException("Nurse not found with id " + nurseId));

        queueEntryRepository.findCurrentForPatient(patientId, List.of(QueueStatus.WAITING, QueueStatus.IN_CONSULT))
                .ifPresent(entry -> {
                    throw new BusinessRuleException("Patient is already in the queue (entry id " + entry.getId() + ")");
                });

        QueueEntry entry = new QueueEntry();
        entry.setPatient(patient);
        entry.setCreatedBy(nurse);
        entry.enqueue();

        QueueEntry saved = queueEntryRepository.save(entry);

        auditLogRepository.save(new AuditLog("QUEUE_ENQUEUE",
                "Patient " + patientId + " enqueued with entry " + saved.getId(), nurse));

        return saved;
    }

    @Transactional
    public QueueEntry markInConsultation(Long queueEntryId) {
        QueueEntry entry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found with id " + queueEntryId));

        if (entry.getStatus() != QueueStatus.WAITING) {
            throw new BusinessRuleException("Only waiting entries can be moved to consultation");
        }

        entry.markInConsultation();
        return queueEntryRepository.save(entry);
    }

    @Transactional
    public QueueEntry markDone(Long queueEntryId) {
        QueueEntry entry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found with id " + queueEntryId));

        if (entry.getStatus() == QueueStatus.DONE) {
            return entry;
        }

        entry.dequeue();
        return queueEntryRepository.save(entry);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<QueueEntry> getActiveQueue() {
        return queueEntryRepository.findActiveQueue();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<QueueEntry> getHistoryForPatient(Long patientId) {
        return queueEntryRepository.findByPatientId(patientId);
    }
}
