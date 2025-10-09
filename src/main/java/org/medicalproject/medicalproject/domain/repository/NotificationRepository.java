package org.medicalproject.medicalproject.domain.repository;

import org.medicalproject.medicalproject.domain.model.Notification;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findByRecipientId(Long recipientId);

    List<Notification> findUnreadByRecipientId(Long recipientId);
}
