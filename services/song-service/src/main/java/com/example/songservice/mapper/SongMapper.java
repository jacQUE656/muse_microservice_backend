package com.example.songservice.mapper;


import com.example.common_lib.payload.DTO.SongDTO;
import com.example.common_lib.payload.DTO.SongDtoList;
import com.example.songservice.model.Song;

import java.util.List;

public class SongMapper {

    public static SongDTO toDTO(Song song) {
        if (song == null) {
            return null;
        }
        return SongDTO.builder()
                .id(song.getId())
                .name(song.getName())
                .description(song.getDescription())
                .album(song.getAlbum())
                .image(song.getImage())
                .audioFile(song.getAudioFile())
                .duration(song.getDuration())
                .dateAdded(song.getDateAdded())
                .createdBy(song.getCreatedBy())
                .isPublic(song.isPublic())
                .build();
    }
    public static SongDtoList toDtoList(List<Song> songs) {
        if (songs == null) {
            return null;

        }

        List<SongDTO> dtoList = songs.stream()
                .map(SongMapper::toDTO)
                .toList();

        return SongDtoList.builder()
                .success(true)   // Explicitly setting success to true
                .songs(dtoList)
                .build();
    }
}
