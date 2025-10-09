package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.Timeslot;
import org.medicalproject.medicalproject.domain.model.enums.TimeslotStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeslotRepository extends CrudRepository<Timeslot, Long> {

    List<Timeslot> findByProfileId(Long profileId);

    List<Timeslot> findAvailableBetween(Long profileId, LocalDateTime start, LocalDateTime end);

    List<Timeslot> findByStatus(TimeslotStatus status);
}
