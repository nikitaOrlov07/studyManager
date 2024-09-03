package com.example.authenticationservice.Service;

import com.example.authenticationservice.Dto.UserDto;

public interface UserService {

    UserDto getUserByNameAndPassword(String username, String password);
}
