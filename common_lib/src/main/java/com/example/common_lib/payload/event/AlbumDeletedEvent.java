package com.example.common_lib.payload.event;

import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDeletedEvent {
    private String albumName;
    private String deletedByUserId;
    private String creatorEmail;
    private String fcmToken;
}