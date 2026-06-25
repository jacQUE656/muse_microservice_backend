package com.example.userservice.service;

import com.example.common_lib.Response.AuthResponse;
import com.example.common_lib.Response.LoginResponse;
import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.LoginRequest;
import com.example.common_lib.payload.Request.RegisterRequest;


public interface AuthService {

     AuthResponse signIn(LoginRequest request);

    UserDTO signUp(RegisterRequest req) ;
}