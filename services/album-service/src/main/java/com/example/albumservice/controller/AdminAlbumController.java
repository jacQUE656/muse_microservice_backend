package com.example.albumservice.controller;

import com.example.albumservice.service.AlbumService;
import com.example.common_lib.Response.ApiResponse;
import com.example.common_lib.payload.DTO.AlbumDtoList;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.AlbumRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/album")
public class AdminAlbumController {

    private final AlbumService albumService;

    @GetMapping
    public ResponseEntity<AlbumDtoList> getAllAlbums(){
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAlbum(@PathVariable String id,
                                                   @RequestBody AlbumRequest albumRequest,
                                                   UserDTO userDTO
                                             ){
        albumService.updateToPublicAlbum(id, albumRequest, userDTO);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Successfully updated album");
        return ResponseEntity.ok(apiResponse);
    }
}
