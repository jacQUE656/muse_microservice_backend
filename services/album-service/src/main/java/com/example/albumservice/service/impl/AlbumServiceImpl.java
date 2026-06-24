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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final Cloudinary cloudinary;

    private String uploadToCloudinary(org.springframework.web.multipart.MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "muse/album_covers",
                    "resource_type", "image"
            ));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload album cover art to Cloudinary", e);
        }
    }

    @Override
    @Transactional
    public void addAlbum(AlbumRequest request, UserDTO user) {
        albumRepository.findByName(request.getName())
                .ifPresent(existingAlbum -> {
                    throw new BusinessException(ErrorCode.ALBUM_ALREADY_EXIST);
                });

        String cover = uploadToCloudinary(request.getCoverFile());

        Album album = Album.builder()
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .coverUrl(cover)
                .createdBy(user.getId())
                .isPublic(false)
                .build();

        albumRepository.save(album);
    }

    @Override
    @Transactional
    public AlbumDtoList getAllAlbums() {
        List<Album> albums = albumRepository.findAll();
        if (albums.isEmpty()) {throw new BusinessException(ErrorCode.ALBUM_NOT_FOUND);}
        return AlbumMapper.toDtoList(albums);
    }

    @Override
    @Transactional
    public AlbumDTO getAlbumById(String id) {
        Album album = albumRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.ALBUM_NOT_FOUND));
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
        // 1. Fetch the existing album or throw 404
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALBUM_NOT_FOUND));

        // 2. Authorize: Ensure the current user owns this album
        if (!album.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 3. Handle optional duplicate name check (if the name is being changed)
        if (request.getName() != null && !request.getName().equals(album.getName())) {
            albumRepository.findByName(request.getName())
                    .ifPresent(existingAlbum -> {
                        throw new BusinessException(ErrorCode.ALBUM_ALREADY_EXIST);
                    });
            album.setName(request.getName());
        }

        // 4. Update optional metadata text fields
        if (request.getDescription() != null) {
            album.setDescription(request.getDescription());
        }
        if (request.getBgColor() != null) {
            album.setBgColor(request.getBgColor());
        }

        // 5. Handle optional cover image replacement
        if (request.getCoverFile() != null && !request.getCoverFile().isEmpty()) {
            // Optional: Delete the old image from Cloudinary here before replacing it

            String newCoverUrl = uploadToCloudinary(request.getCoverFile());
            album.setCoverUrl(newCoverUrl);
        }

        // 6. Persist the updated entity back to the database
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
    @Transactional
    public AlbumDtoList getAllPublicAlbum() {
        List<Album> albums = albumRepository.findByIsPublicTrue();
        if (albums.isEmpty()) {
            throw new BusinessException(ErrorCode.ALBUM_NOT_FOUND);
        }
        return AlbumMapper.toDtoList(albums);
    }

    @Override
    @Transactional
    public void updateToPublicAlbum(String id ,AlbumRequest request, UserDTO user) {
        Album album = albumRepository.findById(id)
                .orElseThrow(()-> new BusinessException(ErrorCode.ALBUM_NOT_FOUND));
        if (!album.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        album.setPublic(true);

    }
}
