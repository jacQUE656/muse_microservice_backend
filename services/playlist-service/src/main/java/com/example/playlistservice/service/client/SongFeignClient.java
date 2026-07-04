package com.example.playlistservice.service.client;

import com.example.common_lib.payload.DTO.SongDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("SONG-SERVICE")
public interface SongFeignClient {

    @GetMapping("/api/songs/batch")
    ResponseEntity<List<SongDTO>> getSongsByIds(@RequestParam("ids") List<String> ids);

    @GetMapping("/api/songs/{id}")
     ResponseEntity<SongDTO> getSongById(@PathVariable String id);
}
