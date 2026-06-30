package com.example.songservice.controller;

import com.example.common_lib.payload.DTO.SongDtoList;
import com.example.songservice.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/songs")
public class AdminSongController {
    private final SongService songService;

    @GetMapping
    public ResponseEntity<SongDtoList> getAllSongs(){
        return ResponseEntity.ok(songService.getAllSongs());
    }
}
