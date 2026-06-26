package com.example.userservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UserServiceApplication {

    public static void main(String[] args) {
        // Look for the .env file at the project root folder and load its variables
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Inject each key-value pair into the System properties so Spring can resolve them
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(UserServiceApplication.class, args);
    }

}