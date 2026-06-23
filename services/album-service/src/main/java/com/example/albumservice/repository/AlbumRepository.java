package com.example.albumservice.repository;

import com.example.albumservice.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, String> {
}
