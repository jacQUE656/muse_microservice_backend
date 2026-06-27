package com.example.userservice.service;


import com.example.common_lib.payload.enums.UserRole;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializerService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        createDefaultAdmin();
        createDefaultPremiumUser();
    }
    public void createDefaultAdmin(){
        if (!userRepository.existsByEmailIgnoreCase("admin565@gmail.com")){
            User admin = User.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .email("admin565@gmail.com")
                    .phone("0123456789")
                    .password(passwordEncoder.encode("admin:65"))
                    .role(UserRole.ADMIN)
                    .emailVerified(true)
                    .build();
            userRepository.save(admin);
            log.info("Default admin created : email = admin65@gmail.com , password = admin65 ");
        }
        else {
            log.info("Admin already exists");
        }
    }
    public void createDefaultPremiumUser(){
        if (!userRepository.existsByEmailIgnoreCase("premiumuser@gmail.com")){
            User pUser = User.builder()
                    .firstName("premiumUser")
                    .lastName("premiumUser")
                    .email("premiumuser@gmail.com")
                    .phone("01234567890")
                    .password(passwordEncoder.encode("premiumUser:56"))
                    .role(UserRole.PREMIUM_USER)
                    .emailVerified(true)
                    .build();
            userRepository.save(pUser);
            log.info("Default Super admin created : email = premiumuser@gmail.com , password = premiumUser:56 ");
        }
        else {
            log.info("Premium User already exists");
        }
    }
}
