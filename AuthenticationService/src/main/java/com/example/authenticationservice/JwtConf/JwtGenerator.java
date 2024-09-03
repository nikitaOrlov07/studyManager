package com.example.authenticationservice.JwtConf;

import com.example.authenticationservice.Dto.UserDto;

import java.util.Map;

public interface JwtGenerator {
    Map<String, String> generateToken(UserDto user);
}
