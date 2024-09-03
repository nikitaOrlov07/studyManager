package com.example.authenticationservice.Dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
