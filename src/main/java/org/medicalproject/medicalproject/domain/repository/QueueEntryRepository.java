package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.QueueEntry;
import org.medicalproject.medicalproject.domain.model.enums.QueueStatus;

import java.util.List;
import java.util.Optional;

public interface QueueEntryRepository extends CrudRepository<QueueEntry, Long> {

    List<QueueEntry> findActiveQueue();

    List<QueueEntry> findByPatientId(Long patientId);

    Optional<QueueEntry> findCurrentForPatient(Long patientId, List<QueueStatus> activeStatuses);
}
