package com.example.songservice.repository;

import com.example.songservice.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, String> {

    Optional<Song> findByName(String name);

    List<Song> findAllByCreatedBy(String createdBy);

    List<Song> findAllByIsPublicTrue();
}