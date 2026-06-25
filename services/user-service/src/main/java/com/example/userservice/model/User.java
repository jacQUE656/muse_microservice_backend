package com.example.userservice.model;

import com.example.common_lib.payload.DTO.AlbumDTO;
import com.example.common_lib.payload.DTO.PlaylistDTO;
import com.example.common_lib.payload.DTO.SongDTO;
import com.example.common_lib.payload.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id")
    private String id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    // Optional Note: Make sure your DB handles enums properly (e.g., @Enumerated(EnumType.STRING))
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private String password;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime lastLogin;

    private String profileImage;

    private boolean emailVerified;
    // Fixed: @Transient tells Hibernate to completely ignore these fields for DB persistence
    @Transient
    @Builder.Default
    private List<PlaylistDTO> playlists = new ArrayList<>();

    @Transient
    @Builder.Default
    private List<SongDTO> songs = new ArrayList<>();

    @Transient
    @Builder.Default
    private List<AlbumDTO> albums = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}