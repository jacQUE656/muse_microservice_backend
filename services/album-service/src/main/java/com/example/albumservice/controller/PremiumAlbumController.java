package com.example.albumservice.controller;

import com.example.albumservice.service.AlbumService;
import com.example.albumservice.service.client.UserFeignClient;
import com.example.common_lib.Response.ApiResponse;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.AlbumRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/premium/albums")
public class PremiumAlbumController {
    private  final AlbumService albumService;
    private final UserFeignClient userFeignClient;

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAlbum(@PathVariable String id,
                                                   @RequestBody AlbumRequest albumRequest,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ){
        UserDTO user = userFeignClient.getUserProfileFromJwt(token).getBody();

        albumService.updateToPublicAlbum(id, albumRequest, user);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Successfully updated album");
        return ResponseEntity.ok(apiResponse);
    }
}
