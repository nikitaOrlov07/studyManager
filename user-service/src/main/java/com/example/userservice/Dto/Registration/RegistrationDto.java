package com.example.userservice.Dto.Registration;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegistrationDto {
    private String username;
    private String email;
    private String password;
    private String age;
    private String town;
    private String phoneNumber;
}

