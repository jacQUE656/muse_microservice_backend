package com.example.common_lib.payload.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumDtoList {
    private boolean success;
    @JsonProperty("albums")
    private List<AlbumDTO> albums;
}
