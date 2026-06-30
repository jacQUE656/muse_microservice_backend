package com.example.playlistservice.service;

import com.example.common_lib.payload.DTO.PlaylistDTO;
import com.example.common_lib.payload.DTO.PlaylistDtoList;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.PlaylistRequest;
import org.springframework.transaction.annotation.Transactional;


public interface PlaylistService {

    void createPlaylist(PlaylistRequest request, UserDTO user);

    PlaylistDTO getPlaylistById(String playlistId);

    PlaylistDtoList getAllPlaylists();

    PlaylistDtoList getAllPublicPlaylists();

    PlaylistDtoList getUserPlaylists(UserDTO user);

    void addSongToPlaylist(String playlistId, String songId, UserDTO user);

    void removeSongFromPlaylist(String playlistId, String songId, UserDTO user);

    void updatePlaylist(String playlistId, PlaylistRequest request, UserDTO user);

    void deletePlaylist(String playlistId, UserDTO user);

    void makePlaylistPublic(String playlistId, UserDTO user);

    void adminDeletePlaylistById(String playlistId);
}