package com.example.userservice.service;


import com.example.common_lib.payload.DTO.UserDTO;
import com.example.common_lib.payload.Request.UpdateProfileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface UserService {


    List<UserDTO> getAllUser();

    Boolean deleteUser(String id);


    UserDTO getUserFromJwt(String token);

    UserDTO getUserProfileFromJwt(String token);

    UserDTO updateProfile(String id , UpdateProfileRequest request, MultipartFile file) throws IOException;

}
