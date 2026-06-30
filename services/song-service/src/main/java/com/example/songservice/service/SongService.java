package com.example.songservice.service;

import com.example.common_lib.payload.DTO.SongDTO;
import com.example.common_lib.payload.DTO.SongDtoList;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.SongRequest;

import java.io.IOException;
import java.util.List;


public interface SongService {
    void uploadSong(SongRequest request , UserDTO user) throws IOException;
    SongDtoList getAllPublicSongs();
    SongDtoList getAllSongs();
    SongDtoList getAllUserSongs(UserDTO user);
    SongDTO getSongById(String id);
    void deleteSongById(String id ,  UserDTO user);
    void updateToPublicSong(String songId, UserDTO user);
    SongDtoList getSongsByIds(List<String> ids);

}
