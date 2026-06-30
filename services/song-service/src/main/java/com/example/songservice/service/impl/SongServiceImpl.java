package com.example.songservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.common_lib.msException.BusinessException;
import com.example.common_lib.payload.DTO.SongDTO;
import com.example.common_lib.payload.DTO.SongDtoList;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.SongRequest;
import com.example.common_lib.payload.enums.ErrorCode;
import com.example.common_lib.payload.enums.UserRole;
import com.example.songservice.mapper.SongMapper;
import com.example.songservice.model.Song;
import com.example.songservice.repository.SongRepository;
import com.example.songservice.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final Cloudinary cloudinary;

    private String formatDuration(Double durationSeconds) {
        if (durationSeconds == null) {
            return "0:00";
        }
        int minutes = (int) (durationSeconds / 60);
        int seconds = (int) (durationSeconds % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    private String uploadSongCoverToCloudinary(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "muse/song_cover",
                    "resource_type", "image"
            ));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            log.error("Cloudinary cover upload failed", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private Map<String, Object> uploadAudioToCloudinary(MultipartFile file) {
        try {
            return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "muse/song_file",
                    "resource_type", "video"
            ));
        } catch (IOException e) {
            log.error("Cloudinary audio upload failed", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    @Transactional
    public void uploadSong(SongRequest request, UserDTO user) {

        // 1. Validate required audio file is present
        if (request.getAudioFile() == null || request.getAudioFile().isEmpty()) {
            throw new BusinessException(ErrorCode.AUDIO_FILE_REQUIRED);
        }

        // 2. Check for duplicate song name (not ID — IDs are server-generated on create)
        songRepository.findByName(request.getName())
                .ifPresent(existing -> {
                    throw new BusinessException(ErrorCode.SONG_ALREADY_EXIST);
                });

        boolean isPublic = UserRole.ADMIN.equals(user.getRole());

        // 3. Upload audio (required)
        Map<String, Object> audioUploadResult = uploadAudioToCloudinary(request.getAudioFile());
        Double durationSeconds = (Double) audioUploadResult.get("duration");
        String duration = formatDuration(durationSeconds);

        // 4. Upload cover image (optional)
        String coverUrl = null;
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            coverUrl = uploadSongCoverToCloudinary(request.getImageFile());
        }

        Song song = Song.builder()
                .name(request.getName())
                .description(request.getDescription())
                .album(request.getAlbum())
                .image(coverUrl)
                .audioFile(audioUploadResult.get("secure_url").toString())
                .duration(duration)
                .downloadCount(0)
                .dateAdded(LocalDate.now())
                .createdBy(user.getId())
                .isPublic(isPublic)
                .build();

        songRepository.save(song);
    }

    @Override
    @Transactional(readOnly = true)
    public SongDtoList getAllPublicSongs() {
        List<Song> songs = songRepository.findAllByIsPublicTrue();
        if (songs.isEmpty()) {
            throw new BusinessException(ErrorCode.SONG_NOT_FOUND);
        }
        return SongMapper.toDtoList(songs);
    }

    @Override
    @Transactional(readOnly = true)
    public SongDtoList getAllSongs() {
        List<Song> songs = songRepository.findAll();
        if (songs.isEmpty()) {
            throw new BusinessException(ErrorCode.SONG_NOT_FOUND);
        }
        return SongMapper.toDtoList(songs);
    }

    @Override
    @Transactional(readOnly = true)
    public SongDtoList getAllUserSongs(UserDTO user) {
        List<Song> songs = songRepository.findAllByCreatedBy(user.getId());
        if (songs.isEmpty()) {
            throw new BusinessException(ErrorCode.SONG_NOT_FOUND);
        }
        return SongMapper.toDtoList(songs);
    }

    @Override
    @Transactional(readOnly = true)
    public SongDTO getSongById(String id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SONG_NOT_FOUND));
        return SongMapper.toDTO(song);
    }

    @Override
    @Transactional
    public void deleteSongById(String id, UserDTO user) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SONG_NOT_FOUND));

        if (!song.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        songRepository.delete(song);
    }

    @Override
    @Transactional
    public void updateToPublicSong(String songId, UserDTO user) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SONG_NOT_FOUND));

        if (!user.getId().equals(song.getCreatedBy())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        if (!user.getRole().equals(UserRole.PREMIUM_USER)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        song.setPublic(true);
        songRepository.save(song);
    }

    @Override
    @Transactional(readOnly = true)
    public SongDtoList getSongsByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return SongMapper.toDtoList(List.of());
        }
        List<Song> songs = songRepository.findAllById(ids);
        return SongMapper.toDtoList(songs);
    }

}