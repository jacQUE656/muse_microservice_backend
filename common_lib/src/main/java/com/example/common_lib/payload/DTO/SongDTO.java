package com.example.common_lib.payload.DTO;

import lombok.*;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SongDTO {
    private String id;

    private String name;

    private String description;

    private String album;

    private String image;

    private String file;

    private String duration;

    private LocalDate dateAdded;

    private String uploadedBy;
}
