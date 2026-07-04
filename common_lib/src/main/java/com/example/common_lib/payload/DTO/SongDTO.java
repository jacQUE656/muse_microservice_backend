package com.example.common_lib.payload.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String audioFile;

    private String duration;

    private LocalDate dateAdded;

    private String createdBy;

    @JsonProperty("isPublic")
    private Boolean isPublic;
}
