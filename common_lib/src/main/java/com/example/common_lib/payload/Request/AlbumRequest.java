package com.example.common_lib.payload.Request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AlbumRequest {
    private String name;
    private String description;
    private String bgColor;
    private MultipartFile imageFile;

}
