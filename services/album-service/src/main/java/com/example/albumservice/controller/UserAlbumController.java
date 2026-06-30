package com.example.albumservice.controller;

import com.example.albumservice.service.AlbumService;
import com.example.albumservice.service.client.UserFeignClient;
import com.example.common_lib.Response.ApiResponse;
import com.example.common_lib.payload.DTO.AlbumDTO;
import com.example.common_lib.payload.DTO.AlbumDtoList;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.AlbumRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/albums")
public class UserAlbumController {

    private final AlbumService albumService;
    private final UserFeignClient userFeignClient;
    private final ObjectMapper objectMapper;


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createAlbum(
            @RequestPart("request") String request,
            @RequestPart(value = "coverFile", required = false) MultipartFile coverFile,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        try {
            UserDTO user = getUserFromToken(token);

            AlbumRequest albumRequest = objectMapper.readValue(request, AlbumRequest.class);

            if (coverFile != null && !coverFile.isEmpty()) {
                albumRequest.setCoverFile(coverFile);
            }

            albumService.addAlbum(albumRequest, user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Album added successfully"));

        } catch (Exception e) {
            log.error("Failed to create album", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to create album: " + e.getMessage()));
        }
    }

    private UserDTO getUserFromToken(String token) {
        UserDTO user = userFeignClient.getUserProfileFromJwt(token).getBody();
        return user;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDTO> getAlbumById(@PathVariable String id) {
        AlbumDTO albumDTO = albumService.getAlbumById(id);
        return ResponseEntity.ok(albumDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAlbumById(
            @PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDTO user = getUserFromToken(token);
        albumService.deleteAlbumById(id, user);
        return ResponseEntity.ok(new ApiResponse("Album deleted successfully"));
    }

    @GetMapping("/user")
    public ResponseEntity<AlbumDtoList> getUserAlbumByUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDTO user = getUserFromToken(token);
        AlbumDtoList albums = albumService.getAllAlbumsByUserId(user);
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/public")
    public ResponseEntity<AlbumDtoList> getAllPublicAlbum() {
        AlbumDtoList albums = albumService.getAllPublicAlbum();
        return ResponseEntity.ok(albums);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> updateAlbumById(
            @PathVariable String id,
            @RequestPart("request") String request,
            @RequestPart(value = "coverFile", required = false) MultipartFile coverFile,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        try {
            UserDTO user = getUserFromToken(token);
            AlbumRequest albumRequest = objectMapper.readValue(request, AlbumRequest.class);

            if (coverFile != null && !coverFile.isEmpty()) {
                albumRequest.setCoverFile(coverFile);
            }

            albumService.updateAlbumById(id, albumRequest, user);
            return ResponseEntity.ok(new ApiResponse("Album updated successfully"));

        } catch (Exception e) {
            log.error("Failed to update album {}", id, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update album: " + e.getMessage()));
        }
    }
}