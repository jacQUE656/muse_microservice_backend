package com.example.common_lib.payload.Request;



import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PlaylistRequest {
    private  String name;
    private String description;
    private String ownerId;
    private MultipartFile imageFile;
    private boolean isPublic;


}
