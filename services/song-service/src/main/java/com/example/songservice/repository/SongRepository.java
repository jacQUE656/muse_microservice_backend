package com.example.songservice.repository;

import com.example.songservice.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, String> {

    Optional<Song> findByName(String name);

    List<Song> findAllByCreatedBy(String createdBy);

    List<Song> findByAlbum(String album);

    @Query("SELECT s FROM Song s JOIN s.playlistIds p WHERE p = :playlistId")
    List<Song> findAllByPlaylistId(@Param("playlistId") String playlistId);

    boolean existsByName(String name);

    List<Song> findAllByIsPublicTrue();
}