package com.example.authenticationservice.Service.impl;

import com.example.authenticationservice.Dto.UserDto;
import com.example.authenticationservice.Exceptions.UserNotFoundException;
import com.example.authenticationservice.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private WebClient.Builder webClientBuilder; // for HTTP requests

    @Override
    public UserDto getUserByNameAndPassword(String username, String password) {
        UserDto userDto = null;
        // HTTP request to userService to get user by username
        userDto = webClientBuilder.build()
                .get()
                .uri("http://user-service/users/" + username)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        if(userDto == null) {
            throw new UserNotFoundException("User not found");
        }

        // Send username and password to userService for verification
        Boolean isAuthenticated = webClientBuilder.build()
                .post()
                .uri("http://user-service/users/checkAuthentication")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("username", username)
                        .with("password", password))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if(isAuthenticated == null || !isAuthenticated) {
            throw new UserNotFoundException("Wrong username or password");
        }

        return userDto;
    }
}
