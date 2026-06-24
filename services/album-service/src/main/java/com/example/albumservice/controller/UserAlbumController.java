package com.example.albumservice.controller;

import com.example.albumservice.service.AlbumService;
import com.example.common_lib.Response.ApiResponse;
import com.example.common_lib.payload.DTO.AlbumDTO;
import com.example.common_lib.payload.DTO.AlbumDtoList;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.AlbumRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/album")
public class UserAlbumController {
    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<ApiResponse> createAlbum(@RequestBody AlbumRequest request ,
                                                   UserDTO user){
        albumService.addAlbum(request,user);
        ApiResponse response = new ApiResponse("Album added successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDTO> getAlbumById(@PathVariable String id){
        AlbumDTO albumDTO = albumService.getAlbumById(id);
        return ResponseEntity.status(HttpStatus.OK).body(albumDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAlbumById(@PathVariable String id, UserDTO user){
        albumService.deleteAlbumById(id , user);
        ApiResponse response = new ApiResponse("Album deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user")
    public ResponseEntity<AlbumDtoList> getUserAlbumById(@RequestBody UserDTO user){
       AlbumDtoList albums = albumService.getAllAlbumsByUserId(user);
       return ResponseEntity.status(HttpStatus.OK).body(albums);
    }

    @GetMapping("/public")
    public ResponseEntity<AlbumDtoList> getAllPublicAlbum(){
        AlbumDtoList albums = albumService.getAllPublicAlbum();
        return ResponseEntity.status(HttpStatus.OK).body(albums);
    }

@PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAlbumById(@PathVariable String id, @RequestBody AlbumRequest request ,UserDTO user){
        albumService.updateAlbumById(id,request,user);
        ApiResponse response = new ApiResponse("Album updated successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
}

}
