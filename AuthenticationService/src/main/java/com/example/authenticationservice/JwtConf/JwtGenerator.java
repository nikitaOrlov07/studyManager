package com.example.authenticationservice.JwtConf;

import com.example.authenticationservice.Dto.UserEntityDto;

import java.util.Map;

public interface JwtGenerator {
    Map<String, Object> generateToken(UserEntityDto user);
}
