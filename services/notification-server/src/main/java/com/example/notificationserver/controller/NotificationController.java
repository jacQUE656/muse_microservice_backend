package com.example.notificationserver.controller;


import com.example.common_lib.Response.ApiResponse;
import com.example.notificationserver.model.Notification;
import com.example.notificationserver.service.InAppNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final InAppNotificationService inAppService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getAll(
            @PathVariable String userId) {
        return ResponseEntity.ok(inAppService.getAll(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnread(
            @PathVariable String userId) {
        return ResponseEntity.ok(inAppService.getUnread(userId));
    }

    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @PathVariable String userId) {
        return ResponseEntity.ok(
                Map.of("count", inAppService.getUnreadCount(userId)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable String id) {
        inAppService.markAsRead(id);
        return ResponseEntity.ok(new ApiResponse("Notification marked as read"));
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse> markAllAsRead(
            @PathVariable String userId) {
        inAppService.markAllAsRead(userId);
        return ResponseEntity.ok(
                new ApiResponse("All notifications marked as read"));
    }
}
