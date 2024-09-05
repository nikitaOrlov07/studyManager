package com.example.authenticationservice.Service;

import com.example.authenticationservice.Dto.UserEntityDto;

public interface UserService {

    UserEntityDto getUserByNameAndPassword(String username, String password);
}
