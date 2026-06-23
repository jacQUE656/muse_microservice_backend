package com.example.albumservice.model;


import com.example.common_lib.payload.DTO.SongDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "albums")
@EntityListeners(AuditingEntityListener.class)
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id")
    private String id;

    private String name;

    private String description;

    private String bgColor;

    private String imageUrl;

    @CreatedBy
    @JsonProperty("createdBy")
    private String createdBy;

}

