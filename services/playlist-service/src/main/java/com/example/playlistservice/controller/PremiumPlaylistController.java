package com.example.playlistservice.controller;

import com.example.common_lib.Response.ApiResponse;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.playlistservice.service.PlaylistService;
import com.example.playlistservice.service.client.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/premium/playlists")
public class PremiumPlaylistController {

    private final PlaylistService playlistService;
    private final UserFeignClient userFeignClient;

    @PatchMapping("/{id}/public")
    public ResponseEntity<ApiResponse> makePlaylistPublic(
            @PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDTO user = getUserFromToken(token);
        playlistService.makePlaylistPublic(id, user);
        return ResponseEntity.ok(new ApiResponse("Playlist made public successfully"));
    }

    private UserDTO getUserFromToken(String token) {
        return userFeignClient.getUserProfileFromJwt(token).getBody();
    }
}