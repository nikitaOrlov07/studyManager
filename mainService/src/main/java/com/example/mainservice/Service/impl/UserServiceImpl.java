package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.User.RegistrationDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Service.UserService;
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
    public String saveUser(RegistrationDto registrationDto) {


        log.info("Sending request to save user: {}", registrationDto);

        // HTTP запрос к user-service для регистрации пользователя
        String result = webClientBuilder.build()
                .post()
                .uri("http://user-service/users/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("username", registrationDto.getUsername())
                        .with("email", registrationDto.getEmail())
                        .with("password", registrationDto.getPassword())
                        .with("town", registrationDto.getTown() != null ? registrationDto.getTown() : "")
                        .with("phoneNumber", registrationDto.getPhoneNumber() != null ? String.valueOf(registrationDto.getPhoneNumber()) : ""))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("Received response: {}", result);

        return result;
    }

    @Override
    public UserEntityDto findUserByUsername(String username) {
        log.info("MainService \"findUserByUsername \" service method is working");
        UserEntityDto userEntityDto = webClientBuilder.build()
                .get()
                .uri("http://user-service/users/"+username)
                .retrieve()
                .bodyToMono(UserEntityDto.class)
                .block();

        return userEntityDto;

    }

}
