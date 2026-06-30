package com.example.common_lib.payload.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SongDtoList {
    private boolean success;
    @JsonProperty("songs")
    private List<SongDTO> songs;
}
