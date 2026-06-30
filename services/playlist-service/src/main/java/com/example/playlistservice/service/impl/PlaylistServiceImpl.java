package com.example.playlistservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.common_lib.msException.BusinessException;
import com.example.common_lib.payload.DTO.PlaylistDTO;
import com.example.common_lib.payload.DTO.PlaylistDtoList;
import com.example.common_lib.payload.DTO.SongDTO;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.PlaylistRequest;
import com.example.common_lib.payload.enums.ErrorCode;
import com.example.common_lib.payload.enums.UserRole;
import com.example.playlistservice.mapper.PlaylistMapper;
import com.example.playlistservice.model.Playlist;
import com.example.playlistservice.repository.PlaylistRepository;
import com.example.playlistservice.service.PlaylistService;
import com.example.playlistservice.service.client.SongFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final Cloudinary cloudinary;
    private final SongFeignClient songFeignClient;

    private String uploadToCloudinary(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "muse/playlist_covers",
                    "resource_type", "image"
            ));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            log.error("Cloudinary upload failed", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private Set<SongDTO> resolveSongs(Set<String> songIds) {
        if (songIds == null || songIds.isEmpty()) {
            return Set.of();
        }
        List<SongDTO> songs = songFeignClient
                .getSongsByIds(new ArrayList<>(songIds))
                .getBody();
        return songs != null ? new HashSet<>(songs) : Set.of();
    }

    private PlaylistDTO toEnrichedDto(Playlist playlist) {
        Set<SongDTO> songs = resolveSongs(playlist.getSongIds());
        return PlaylistMapper.toDto(playlist, songs);
    }

    @Override
    @Transactional
    public void createPlaylist(PlaylistRequest request, UserDTO user) {

        playlistRepository.findByNameAndCreatedBy(request.getName(), user.getId())
                .ifPresent(existing -> {
                    throw new BusinessException(ErrorCode.PLAYLIST_ALREADY_EXIST);
                });

        String coverUrl = null;
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            coverUrl = uploadToCloudinary(request.getImageFile());
        }

        boolean isPublic = UserRole.ADMIN.equals(user.getRole());

        Playlist playlist = Playlist.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(coverUrl)
                .isPublic(isPublic)
                .createdBy(user.getId())
                .build();

        playlistRepository.save(playlist);
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistDTO getPlaylistById(String playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));
        return toEnrichedDto(playlist);
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistDtoList getAllPlaylists() {
        List<Playlist> playlists = playlistRepository.findAll();
        if (playlists.isEmpty()) {
            throw new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND);
        }
        List<PlaylistDTO> dtos = playlists.stream()
                .map(this::toEnrichedDto)
                .collect(Collectors.toList());
        return new PlaylistDtoList(true, dtos);
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistDtoList getAllPublicPlaylists() {
        List<Playlist> playlists = playlistRepository.findByIsPublicTrue();
        if (playlists.isEmpty()) {
            throw new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND);
        }
        List<PlaylistDTO> dtos = playlists.stream()
                .map(this::toEnrichedDto)
                .collect(Collectors.toList());
        return new PlaylistDtoList(true, dtos);
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistDtoList getUserPlaylists(UserDTO user) {
        List<Playlist> playlists = playlistRepository.findAllByCreatedBy(user.getId());
        if (playlists.isEmpty()) {
            throw new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND);
        }
        List<PlaylistDTO> dtos = playlists.stream()
                .map(this::toEnrichedDto)
                .collect(Collectors.toList());
        return new PlaylistDtoList(true, dtos);
    }

    @Override
    @Transactional
    public void addSongToPlaylist(String playlistId, String songId, UserDTO user) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        if (!playlist.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        SongDTO song = songFeignClient.getSongById(songId).getBody();
        if (song == null) {
            throw new BusinessException(ErrorCode.SONG_NOT_FOUND);
        }

        playlist.getSongIds().add(songId);
        playlistRepository.save(playlist);
    }

    @Override
    @Transactional
    public void removeSongFromPlaylist(String playlistId, String songId, UserDTO user) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        if (!playlist.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        playlist.getSongIds().remove(songId);
        playlistRepository.save(playlist);
    }

    @Override
    @Transactional
    public void updatePlaylist(String playlistId, PlaylistRequest request, UserDTO user) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        if (!playlist.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (request.getName() != null && !request.getName().equals(playlist.getName())) {
            playlistRepository.findByNameAndCreatedBy(request.getName(), user.getId())
                    .ifPresent(existing -> {
                        throw new BusinessException(ErrorCode.PLAYLIST_ALREADY_EXIST);
                    });
            playlist.setName(request.getName());
        }

        if (request.getDescription() != null) {
            playlist.setDescription(request.getDescription());
        }

        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            String newCoverUrl = uploadToCloudinary(request.getImageFile());
            playlist.setImageUrl(newCoverUrl);
        }

        playlistRepository.save(playlist);
    }

    @Override
    @Transactional
    public void deletePlaylist(String playlistId, UserDTO user) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        if (!playlist.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        playlistRepository.delete(playlist);
    }

    @Override
    @Transactional
    public void makePlaylistPublic(String playlistId, UserDTO user) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));

        boolean isAdmin = UserRole.ADMIN.equals(user.getRole());
        boolean isOwner = playlist.getCreatedBy().equals(user.getId());
        boolean isPremium = UserRole.PREMIUM_USER.equals(user.getRole());

        if (!isAdmin && !(isOwner && isPremium)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        playlist.setPublic(true);
        playlistRepository.save(playlist);
    }

    @Transactional
    @Override
    public void adminDeletePlaylistById(String playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYLIST_NOT_FOUND));
        playlistRepository.delete(playlist);
    }
}