package com.example.common_lib.payload.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumCreatedEvent {
    private String albumId;
    private String albumName;
    private String createdByUserId;
    private String creatorEmail;
    private String fcmToken;
}