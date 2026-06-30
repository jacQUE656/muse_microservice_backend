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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/songs")
public class UserSongController {
    private final SongService songService;
    private UserFeignClient userFeignClient;
    private final ObjectMapper objectMapper;


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> uploadSong(
            @RequestPart("request") String requestString,
            @RequestPart("audio") MultipartFile audioFile,
            @RequestPart("image") MultipartFile imageFile,
            @RequestHeader("Authorization") String token
    ) {
        try{

            UserDTO user = userFeignClient.getUserProfileFromJwt(token).getBody();
            SongRequest songRequest = objectMapper.readValue(requestString, SongRequest.class);
            if (imageFile != null && !imageFile.isEmpty()) {
                songRequest.setImageFile(imageFile);
            }
            if (audioFile != null && !audioFile.isEmpty()) {
                songRequest.setAudioFile(audioFile);
            }
            songService.uploadSong(songRequest,user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Song uploaded successfully"));

        } catch (Exception e) {
        log.error("Failed to create album", e);
        return ResponseEntity.badRequest()
                .body(new ApiResponse("Failed to create album: " + e.getMessage()));
    }

    }
    @GetMapping("/public")
    public ResponseEntity<SongDtoList> geAllPublicSongs(){
        SongDtoList songDtoList = songService.getAllPublicSongs();
        return ResponseEntity.status(HttpStatus.OK).body(songDtoList);
    }

    @GetMapping("/user")
    public ResponseEntity<SongDtoList> getUserSongs(@RequestHeader("Authorization") String token){
        UserDTO user = userFeignClient.getUserProfileFromJwt(token).getBody();
        SongDtoList songs = songService.getAllUserSongs(user);
        return ResponseEntity.status(HttpStatus.OK).body(songs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable String id){
        SongDTO songDTO = songService.getSongById(id);
        return ResponseEntity.status(HttpStatus.OK).body(songDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSongById(@PathVariable String id , @RequestHeader("Authorization") String token){
        UserDTO user = userFeignClient.getUserProfileFromJwt(token).getBody();
        songService.deleteSongById(id,user);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Song deleted successfully"));
    }
}
