package com.example.albumservice.mapper;

import com.example.albumservice.model.Album;
import com.example.common_lib.payload.DTO.AlbumDTO;
import com.example.common_lib.payload.DTO.AlbumDtoList;

import java.util.List;

public class AlbumMapper {


    public static AlbumDTO toDto(Album album) {
        if (album == null) {
            return null;
        }

        return AlbumDTO.builder()
                .id(album.getId())
                .name(album.getName())
                .description(album.getDescription())
                .bgColor(album.getBgColor())
                .coverUrl(album.getCoverUrl())
                .createdBy(album.getCreatedBy())
                .isPublic(album.isPublic())
                .build();
    }


    public static AlbumDtoList toDtoList(List<Album> albums) {
        if (albums == null) {
            return null;

        }

        List<AlbumDTO> dtoList = albums.stream()
                .map(AlbumMapper::toDto)
                .toList();

        return AlbumDtoList.builder()
                .success(true)   // Explicitly setting success to true
                .albums(dtoList)
                .build();
    }
}