package com.example.songservice.controller;

import com.example.common_lib.Response.ApiResponse;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.songservice.service.SongService;
import com.example.songservice.service.client.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/premium/songs")
public class PremiumSongController {
    private final SongService songService;
    private final UserFeignClient userFeignClient;

    @PutMapping("/(id}")
    public ResponseEntity<ApiResponse> updateToPublic(
            @PathVariable("songId") String songId,
            @RequestHeader("Authorization") String token
            ){
        UserDTO user = userFeignClient.getUserProfileFromJwt(token).getBody();
        songService.updateToPublicSong(songId, user);

        return  ResponseEntity.ok(new ApiResponse("update successful"));

    }
}
