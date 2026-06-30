package com.example.playlistservice.controller;

import com.example.common_lib.Response.ApiResponse;
import com.example.common_lib.payload.DTO.PlaylistDTO;
import com.example.common_lib.payload.DTO.PlaylistDtoList;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.PlaylistRequest;
import com.example.playlistservice.service.PlaylistService;
import com.example.playlistservice.service.client.UserFeignClient;
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
@RequestMapping("/api/playlists")
public class UserPlaylistController {

    private final PlaylistService playlistService;
    private final UserFeignClient userFeignClient;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> createPlaylist(
            @RequestPart("request") String requestString,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        try {
            UserDTO user = getUserFromToken(token);
            PlaylistRequest request = objectMapper.readValue(requestString, PlaylistRequest.class);

            if (imageFile != null && !imageFile.isEmpty()) {
                request.setImageFile(imageFile);
            }
            if(imageFile==null){
                request.setImageFile(null);
            }

            playlistService.createPlaylist(request, user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Playlist created successfully"));

        } catch (Exception e) {
            log.error("Failed to create playlist", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to create playlist: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable String id) {
        return ResponseEntity.ok(playlistService.getPlaylistById(id));
    }

    @GetMapping("/public")
    public ResponseEntity<PlaylistDtoList> getAllPublicPlaylists() {
        return ResponseEntity.ok(playlistService.getAllPublicPlaylists());
    }

    @GetMapping("/user")
    public ResponseEntity<PlaylistDtoList> getUserPlaylists(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDTO user = getUserFromToken(token);
        return ResponseEntity.ok(playlistService.getUserPlaylists(user));
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<ApiResponse> addSongToPlaylist(
            @PathVariable String playlistId,
            @PathVariable String songId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDTO user = getUserFromToken(token);
        playlistService.addSongToPlaylist(playlistId, songId, user);
        return ResponseEntity.ok(new ApiResponse("Song added to playlist successfully"));
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<ApiResponse> removeSongFromPlaylist(
            @PathVariable String playlistId,
            @PathVariable String songId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDTO user = getUserFromToken(token);
        playlistService.removeSongFromPlaylist(playlistId, songId, user);
        return ResponseEntity.ok(new ApiResponse("Song removed from playlist successfully"));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> updatePlaylist(
            @PathVariable String id,
            @RequestPart("request") String requestString,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        try {
            UserDTO user = getUserFromToken(token);
            PlaylistRequest request = objectMapper.readValue(requestString, PlaylistRequest.class);

            if (imageFile != null && !imageFile.isEmpty()) {
                request.setImageFile(imageFile);
            }

            playlistService.updatePlaylist(id, request, user);
            return ResponseEntity.ok(new ApiResponse("Playlist updated successfully"));

        } catch (Exception e) {
            log.error("Failed to update playlist", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update playlist: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePlaylist(
            @PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDTO user = getUserFromToken(token);
        playlistService.deletePlaylist(id, user);
        return ResponseEntity.ok(new ApiResponse("Playlist deleted successfully"));
    }

    private UserDTO getUserFromToken(String token) {
        return userFeignClient.getUserProfileFromJwt(token).getBody();
    }
}