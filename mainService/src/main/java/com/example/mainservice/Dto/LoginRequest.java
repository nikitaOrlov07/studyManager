package com.example.mainservice.Dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String ipAddress;
    private String deviceType;
}
