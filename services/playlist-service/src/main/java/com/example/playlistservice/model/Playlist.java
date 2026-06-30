package com.example.playlistservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "playlists")
@EntityListeners(AuditingEntityListener.class)

public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String description;

    @ElementCollection
    @CollectionTable(
            name = "playlist_song_ids",
            joinColumns = @JoinColumn(name = "playlist_id")
    )
    @Column(name = "song_id")
    @Builder.Default
    private Set<String> songIds = new HashSet<>();

    @CreatedBy
    private String createdBy;

    private String imageUrl;

    private boolean isPublic;
}