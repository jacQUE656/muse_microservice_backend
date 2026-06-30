package com.example.songservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String description;

    private String album;

    private String image;

    private String audioFile;

    private String duration;

    private int downloadCount;

    private LocalDate dateAdded;

    private boolean isPublic = false;

    @CreatedBy
    @JsonProperty("createdBy")
    private String createdBy;
}