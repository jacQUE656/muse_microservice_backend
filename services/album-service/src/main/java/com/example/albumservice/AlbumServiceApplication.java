package com.example.albumservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableFeignClients
@EnableAsync
public class AlbumServiceApplication {

    public static void main(String[] args) {
        // Load the .env variables from the root folder
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Expose them as system properties for Spring Cloud Config to bind to placeholders
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(AlbumServiceApplication.class, args);
    }

}