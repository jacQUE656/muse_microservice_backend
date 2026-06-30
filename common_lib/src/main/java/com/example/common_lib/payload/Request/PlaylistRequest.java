package com.example.common_lib.payload.Request;



import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PlaylistRequest {
    private String name;
    private String description;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MultipartFile imageFile;
}