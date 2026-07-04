package com.example.playlistservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
@EnableDiscoveryClient
@EnableAsync
public class PlaylistServiceApplication {

    public static void main(String[] args) {
        // MUST happen before SpringApplication.run()
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .filename(".env")
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        // Confirm values resolved before Spring starts
        System.out.println(">>> DB_USERNAME = " + System.getProperty("DB_USERNAME"));
        System.out.println(">>> PLAYLIST_DB = " + System.getProperty("PLAYLIST_DB"));

        SpringApplication.run(PlaylistServiceApplication.class, args);
    }
}