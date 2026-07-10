package com.example.notificationserver.repository;

import com.example.notificationserver.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findAllByUserIdOrderByCreatedAtDesc(String userId);

    List<Notification> findAllByUserIdAndReadFalse(String userId);

    long countByUserIdAndReadFalse(String userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    void markAllAsReadByUserId(String userId);

    @Modifying
    void deleteAllByUserId(String userId);
}