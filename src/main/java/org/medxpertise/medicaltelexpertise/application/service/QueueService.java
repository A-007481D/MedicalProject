package org.medxpertise.medicaltelexpertise.application.service;

import org.medxpertise.medicaltelexpertise.application.service.exception.BusinessRuleException;
import org.medxpertise.medicaltelexpertise.domain.model.Nurse;
import org.medxpertise.medicaltelexpertise.domain.model.Patient;
import org.medxpertise.medicaltelexpertise.domain.model.QueueEntry;
import org.medxpertise.medicaltelexpertise.domain.model.enums.QueueStatus;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.NurseRepositoryJpa;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.PatientRepositoryJpa;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.QueueEntryRepositoryJpa;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QueueService {
    
    private final QueueEntryRepositoryJpa queueRepository;
    private final PatientRepositoryJpa patientRepository;
    private final NurseRepositoryJpa nurseRepository;
    
    public QueueService() {
        this.queueRepository = new QueueEntryRepositoryJpa();
        this.patientRepository = new PatientRepositoryJpa();
        this.nurseRepository = new NurseRepositoryJpa();
    }

    public QueueEntry addToQueue(Long patientId, Long nurseId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new BusinessRuleException("Patient not found"));
        
        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new BusinessRuleException("Nurse not found"));
        
        List<QueueEntry> existingEntries = queueRepository.findAll().stream()
                .filter(q -> q.getPatient().getId().equals(patientId) && 
                           q.getStatus() == QueueStatus.WAITING)
                .collect(Collectors.toList());
        
        if (!existingEntries.isEmpty()) {
            throw new BusinessRuleException("Patient is already in the waiting queue");
        }
        
        QueueEntry queueEntry = new QueueEntry();
        queueEntry.setPatient(patient);
        queueEntry.setCreatedBy(nurse);
        queueEntry.setArrivalTime(LocalDateTime.now());
        queueEntry.setStatus(QueueStatus.WAITING);
        
        return queueRepository.save(queueEntry);
    }
    
    public List<QueueEntry> getWaitingQueue() {
        return queueRepository.findAll().stream()
                .filter(q -> q.getStatus() == QueueStatus.WAITING)
                .sorted(Comparator.comparing(QueueEntry::getArrivalTime))
                .collect(Collectors.toList());
    }
    

    public List<QueueEntry> getTodayQueue() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        return queueRepository.findAll().stream()
                .filter(q -> q.getArrivalTime().isAfter(startOfDay) && 
                           q.getArrivalTime().isBefore(endOfDay))
                .sorted(Comparator.comparing(QueueEntry::getArrivalTime))
                .collect(Collectors.toList());
    }

    public QueueEntry markInProgress(Long queueEntryId) {
        QueueEntry entry = queueRepository.findById(queueEntryId)
                .orElseThrow(() -> new BusinessRuleException("Queue entry not found"));
        
        entry.markInConsultation();
        return queueRepository.save(entry);
    }

    public QueueEntry removeFromQueue(Long queueEntryId) {
        QueueEntry entry = queueRepository.findById(queueEntryId)
                .orElseThrow(() -> new BusinessRuleException("Queue entry not found"));
        
        entry.dequeue();
        return queueRepository.save(entry);
    }
}