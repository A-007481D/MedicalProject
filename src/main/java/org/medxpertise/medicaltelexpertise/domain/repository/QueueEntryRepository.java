package org.medxpertise.medicaltelexpertise.domain.repository;

import org.medxpertise.medicaltelexpertise.domain.model.QueueEntry;
import org.medxpertise.medicaltelexpertise.domain.model.enums.QueueStatus;

import java.util.List;
import java.util.Optional;

public interface QueueEntryRepository extends CrudRepository<QueueEntry, Long> {

    List<QueueEntry> findActiveQueue();

    List<QueueEntry> findByPatientId(Long patientId);

    Optional<QueueEntry> findCurrentForPatient(Long patientId, List<QueueStatus> activeStatuses);
}
