package com.example.common_lib.payload.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongDeletedEvent {
    private String songName;
    private String deletedByUserId;
    private String uploaderEmail;
    private String fcmToken;
}