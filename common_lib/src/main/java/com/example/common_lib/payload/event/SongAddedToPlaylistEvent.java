package com.example.common_lib.payload.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongAddedToPlaylistEvent {
    private String playlistId;
    private String playlistName;
    private String songId;
    private String songName;
    private String userId;
    private String userEmail;
    private String fcmToken;
}