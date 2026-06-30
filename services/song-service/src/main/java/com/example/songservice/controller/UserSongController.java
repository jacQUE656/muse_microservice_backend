package com.example.songservice.controller;

import com.example.common_lib.Response.ApiResponse;
import com.example.common_lib.payload.DTO.SongDTO;
import com.example.common_lib.payload.DTO.SongDtoList;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.SongRequest;
import com.example.songservice.service.SongService;
import com.example.songservice.service.client.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/songs")
public class UserSongController {

    private final SongService songService;
    private final UserFeignClient userFeignClient;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> uploadSong(
            @RequestPart("request") String requestString,
            @RequestPart("audio") MultipartFile audioFile,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        try {
            UserDTO user = getUserFromToken(token);

            SongRequest songRequest = objectMapper.readValue(requestString, SongRequest.class);
            songRequest.setAudioFile(audioFile);

            if (imageFile != null && !imageFile.isEmpty()) {
                songRequest.setImageFile(imageFile);
            }

            songService.uploadSong(songRequest, user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Song uploaded successfully"));

        } catch (Exception e) {
            log.error("Failed to upload song", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to upload song: " + e.getMessage()));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<SongDtoList> getAllPublicSongs() {
        SongDtoList songDtoList = songService.getAllPublicSongs();
        return ResponseEntity.ok(songDtoList);
    }

    @GetMapping("/user")
    public ResponseEntity<SongDtoList> getUserSongs(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDTO user = getUserFromToken(token);
        SongDtoList songs = songService.getAllUserSongs(user);
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable String id) {
        SongDTO songDTO = songService.getSongById(id);
        return ResponseEntity.ok(songDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSongById(
            @PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDTO user = getUserFromToken(token);
        songService.deleteSongById(id, user);
        return ResponseEntity.ok(new ApiResponse("Song deleted successfully"));
    }

    private UserDTO getUserFromToken(String token) {
        return userFeignClient.getUserProfileFromJwt(token).getBody();
    }

    @GetMapping("/batch")
    public ResponseEntity<SongDtoList> getSongsByIds(@RequestParam List<String> ids) {
        SongDtoList songs = songService.getSongsByIds(ids);
        return ResponseEntity.ok(songs);
    }
}