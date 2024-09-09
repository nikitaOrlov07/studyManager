package com.example.authenticationservice.Service.impl;

import com.example.authenticationservice.Dto.UserEntityDto;
import com.example.authenticationservice.Exceptions.UserNotFoundException;
import com.example.authenticationservice.Exceptions.WrongDataException;
import com.example.authenticationservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private WebClient.Builder webClientBuilder; // for HTTP requests

    @Override
    public UserEntityDto getUserByNameAndPassword(String username, String password) {
        log.info("UserService \"getUserByNameAndPassword\" working ");
        UserEntityDto userEntityDto = null;
        // HTTP request to userService to get user by username
        userEntityDto = webClientBuilder.build()
                .get()
                .uri("http://user-service/users/" + username)
                .retrieve()
                .bodyToMono(UserEntityDto.class)
                .block();

        if(userEntityDto == null) {
            throw new UserNotFoundException("User not found");
        }
        log.info("userEntity name: " + userEntityDto.getUsername());
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

        if(isAuthenticated == null || isAuthenticated.equals(false)) {
            log.info("Password isn`t correct");
            throw new WrongDataException("Wrong username or password");
        }
        else
            log.info("user is authenticated");

        return userEntityDto;
    }
}
