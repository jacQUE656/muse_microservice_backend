package com.example.notificationserver.service;


import com.example.notificationserver.model.Notification;
import com.example.notificationserver.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InAppNotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void save(String userId, String title, String message) {
        notificationRepository.save(
                Notification.builder()
                        .userId(userId)
                        .title(title)
                        .message(message)
                        .type(Notification.NotificationType.IN_APP)
                        .read(false)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<Notification> getAll(String userId) {
        return notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnread(String userId) {
        return notificationRepository.findAllByUserIdAndReadFalse(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Transactional
    public void delete(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void deleteAll(String userId) {
        notificationRepository.deleteAllByUserId(userId);
    }
}
