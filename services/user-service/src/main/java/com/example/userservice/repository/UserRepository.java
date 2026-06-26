package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByEmailIgnoreCase(String email);
}
