package com.example.common_lib.payload.Request;


import com.example.common_lib.payload.enums.Views;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PlaylistRequest {
    private  String name;
    private String description;
    private String ownerId;
    private MultipartFile imageFile;
    private Views views;


}
