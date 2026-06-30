package com.example.playlistservice.repository;

import com.example.playlistservice.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist,String> {
    Optional<Playlist> findByNameAndUserId(String name, String userId);
    List<Playlist> findAllByCreatedBy(String createdBy);
    List<Playlist> findByIsPublicTrue();

    Optional<Object> findByNameAndCreatedBy(String name, String id);
}
