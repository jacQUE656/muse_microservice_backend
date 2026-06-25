package com.example.common_lib.payload.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDtoList {
    private boolean success;
    @JsonProperty("albums")
    private List<UserDTO> songs;
}
