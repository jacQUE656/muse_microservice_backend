package com.example.common_lib.payload.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongUploadedEvent {
    private String songId;
    private String songName;
    private String uploadedByUserId;
    private String uploaderEmail;
    private String fcmToken;
}