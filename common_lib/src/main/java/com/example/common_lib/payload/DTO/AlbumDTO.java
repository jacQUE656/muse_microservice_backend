package com.example.common_lib.payload.DTO;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumDTO {
    private String id;

    private String name;

    private String description;

    private String bgColor;

    private String imageUrl;

    private String ownerId;

}
