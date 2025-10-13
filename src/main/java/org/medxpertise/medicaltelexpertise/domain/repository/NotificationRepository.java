package org.medxpertise.medicaltelexpertise.domain.repository;


import org.medxpertise.medicaltelexpertise.domain.model.Notification;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findByRecipientId(Long recipientId);

    List<Notification> findUnreadByRecipientId(Long recipientId);
}
