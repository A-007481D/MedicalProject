package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends CrudRepository<AuditLog, Long> {

    List<AuditLog> findByActorId(Long actorId);

    List<AuditLog> findByActionCode(String actionCode);

    List<AuditLog> findBetween(LocalDateTime start, LocalDateTime end);
}
