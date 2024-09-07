package com.example.mainservice.Service;

import com.example.mainservice.Dto.User.RegistrationDto;
import com.example.mainservice.Dto.User.UserEntityDto;

public interface UserService {
    String saveUser(RegistrationDto registrationDto);

    UserEntityDto findUserByUsername(String sessionUser);
}
