package org.graduate.shoefastbe.controller;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.dto.notification.NotificationResponse;
import org.graduate.shoefastbe.entity.Notification;
import org.graduate.shoefastbe.repository.NotificationRepository;
import org.graduate.shoefastbe.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@AllArgsConstructor
@CrossOrigin
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @GetMapping("/load")
    public List<NotificationResponse> loadNotification() {
        return notificationService.loadNotification(false, true);
    }

    @GetMapping("/read")
    public NotificationResponse readNotification(@RequestParam("id") Long id) {
        return notificationService.modifyNotification(id);
    }

    @GetMapping("/push")
    public ResponseEntity<?> pushNotification() {
        // lấy ra cac thông báo sau khi dặt hàng, set deliver => true dể cho load lấy ra nhằm xác dinh thong báo da dc gửi tới admin
        List<Notification> notifications = notificationRepository.getNotificationByReadEqualsAndDeliverEquals(false, false);
        for (Notification n : notifications) {
            n.setDeliver(true);
            notificationService.updateNotification(n);
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
}
