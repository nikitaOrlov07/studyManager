package com.example.mainservice.Service;

import com.example.mainservice.Dto.LoginRequest;
import com.example.mainservice.Dto.RegistrationDto;
import com.example.mainservice.Dto.UserEntityDto;

public interface UserService {
    String saveUser(RegistrationDto registrationDto);

    UserEntityDto findUserByUsername(String sessionUser);
}
