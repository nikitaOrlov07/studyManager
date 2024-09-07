package com.example.mainservice.Dto.User;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegistrationDto {
    @NotEmpty(message = "You need to provide username")
    private String username;
    @NotEmpty(message = "You need to provide email")
    private String email;
    @NotEmpty(message = "You need to provide password")
    private String password;
    @NotEmpty(message = "You need to provide your age")
    private String age;
    @NotEmpty(message = "You need to provide your town")
    private String town;
    @NotEmpty(message = "You need to provide phone number")
    private String phoneNumber;
}
