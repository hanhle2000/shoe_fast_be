package org.graduate.shoefastbe.service;

import org.graduate.shoefastbe.dto.notification.NotificationResponse;
import org.graduate.shoefastbe.entity.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> loadNotification(Boolean read, Boolean deliver);
    NotificationResponse modifyNotification(Long id);
    NotificationResponse updateNotification(Notification notification);
}
