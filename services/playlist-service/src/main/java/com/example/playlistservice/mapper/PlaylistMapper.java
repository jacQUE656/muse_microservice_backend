package com.example.playlistservice.mapper;

import com.example.common_lib.payload.DTO.PlaylistDTO;
import com.example.common_lib.payload.DTO.SongDTO;
import com.example.playlistservice.model.Playlist;

import java.util.Set;

public class PlaylistMapper
{
    public static PlaylistDTO toDto(Playlist playlist , Set<SongDTO> songDTOS) {
        if (playlist == null) return null;
        return PlaylistDTO.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .createdBy(playlist.getCreatedBy())
                .image(playlist.getImageUrl())
                .isPublic(playlist.isPublic())
                .songs(songDTOS)
                .build();
    }

}
