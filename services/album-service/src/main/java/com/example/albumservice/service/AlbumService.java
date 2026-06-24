package com.example.albumservice.service;

import com.example.common_lib.payload.DTO.AlbumDTO;
import com.example.common_lib.payload.DTO.AlbumDtoList;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.AlbumRequest;

public interface AlbumService {
     void addAlbum(AlbumRequest request , UserDTO user);
     AlbumDtoList getAllAlbums();
     AlbumDTO getAlbumById(String id);
     void deleteAlbumById(String id , UserDTO user);
     void updateAlbumById(String albumId , AlbumRequest request , UserDTO user);
     AlbumDtoList getAllAlbumsByUserId(UserDTO user);
     AlbumDtoList getAllPublicAlbum();
     void updateToPublicAlbum(String id ,AlbumRequest request , UserDTO user);


}
