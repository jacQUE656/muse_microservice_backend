package com.example.albumservice.repository;

import com.example.albumservice.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, String> {
    List<Album> findAllByCreatedBy(String createdBy);
    Optional<Album> findByName(String name);
    List<Album> findByIsPublicTrue();

}