package com.example.albumservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.albumservice.mapper.AlbumMapper;
import com.example.albumservice.model.Album;
import com.example.albumservice.repository.AlbumRepository;
import com.example.albumservice.service.AlbumService;
import com.example.common_lib.msException.BusinessException;
import com.example.common_lib.payload.DTO.AlbumDTO;
import com.example.common_lib.payload.DTO.AlbumDtoList;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.AlbumRequest;
import com.example.common_lib.payload.enums.ErrorCode;
import com.example.common_lib.payload.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final Cloudinary cloudinary;

    private String uploadToCloudinary(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "muse/album_covers",
                    "resource_type", "image"
            ));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            log.error("Cloudinary upload failed", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    @Transactional
    public void addAlbum(AlbumRequest request, UserDTO user) {
        // 1. Check for duplicate album name
        albumRepository.findByName(request.getName())
                .ifPresent(existingAlbum -> {
                    throw new BusinessException(ErrorCode.ALBUM_ALREADY_EXIST);
                });

        // 2. Process cover upload only if a file was actually provided
        String coverUrl = null;
        if (request.getCoverFile() != null && !request.getCoverFile().isEmpty()) {
            coverUrl = uploadToCloudinary(request.getCoverFile());
        }

        // 3. Determine visibility (Admin uploads are public by default)
        boolean isPublic = UserRole.ADMIN.equals(user.getRole());

        // 4. Build and persist the entity
        Album album = Album.builder()
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .coverUrl(coverUrl)
                .createdBy(user.getId())
                .isPublic(isPublic)
                .build();

        albumRepository.save(album);
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDtoList getAllAlbums() {
        List<Album> albums = albumRepository.findAll();
        if (albums.isEmpty()) {
            throw new BusinessException(ErrorCode.ALBUM_NOT_FOUND);
        }
        return AlbumMapper.toDtoList(albums);
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDTO getAlbumById(String id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALBUM_NOT_FOUND));
        return AlbumMapper.toDto(album);
    }

    @Override
    @Transactional
    public void deleteAlbumById(String id, UserDTO user) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALBUM_NOT_FOUND));

        if (!album.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        albumRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateAlbumById(String albumId, AlbumRequest request, UserDTO user) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALBUM_NOT_FOUND));

        if (!album.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (request.getName() != null && !request.getName().equals(album.getName())) {
            albumRepository.findByName(request.getName())
                    .ifPresent(existingAlbum -> {
                        throw new BusinessException(ErrorCode.ALBUM_ALREADY_EXIST);
                    });
            album.setName(request.getName());
        }

        if (request.getDescription() != null) {
            album.setDescription(request.getDescription());
        }
        if (request.getBgColor() != null) {
            album.setBgColor(request.getBgColor());
        }

        if (request.getCoverFile() != null && !request.getCoverFile().isEmpty()) {
            // TODO: delete old image from Cloudinary using its public_id before replacing
            String newCoverUrl = uploadToCloudinary(request.getCoverFile());
            album.setCoverUrl(newCoverUrl);
        }

        albumRepository.save(album);
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDtoList getAllAlbumsByUserId(UserDTO user) {
        List<Album> albums = albumRepository.findAllByCreatedBy(user.getId());
        if (albums.isEmpty()) {
            throw new BusinessException(ErrorCode.ALBUM_NOT_FOUND);
        }
        return AlbumMapper.toDtoList(albums);
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDtoList getAllPublicAlbum() {
        List<Album> albums = albumRepository.findByIsPublicTrue();
        if (albums.isEmpty()) {
            throw new BusinessException(ErrorCode.ALBUM_NOT_FOUND);
        }
        return AlbumMapper.toDtoList(albums);
    }

    @Override
    @Transactional
    public void updateToPublicAlbum(String id, AlbumRequest request, UserDTO user) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALBUM_NOT_FOUND));

        if (!album.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        album.setPublic(true);
        albumRepository.save(album);
    }
}