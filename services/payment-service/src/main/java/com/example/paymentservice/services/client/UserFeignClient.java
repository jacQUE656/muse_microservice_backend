package com.example.paymentservice.services.client;

import com.example.common_lib.payload.DTO.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("USER-SERVICE")
public interface UserFeignClient {
    @GetMapping("/api/users/me/profile")
    ResponseEntity<UserDTO> getUserProfileFromJwt(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);


}