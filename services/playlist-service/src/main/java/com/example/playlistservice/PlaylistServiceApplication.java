package com.example.playlistservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PlaylistServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaylistServiceApplication.class, args);
    }

}
