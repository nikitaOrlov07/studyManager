package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.User.RegistrationDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                        .with("age",registrationDto.getAge())
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

    @Override
    public List<UserEntityDto> findUsersByIds(List<Long> involvedUserIds) {
        List<UserEntityDto> users = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("user-service")
                        .path("/users/findUsersByIds")
                        .queryParam("usersIds", involvedUserIds) // Correctly add the query parameter
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserEntityDto>>() {})
                .block();

        if(users == null)
            log.error("List of users is  null");
        else if(users.isEmpty())
            log.error("List of users is empty");
        else
            log.info("List of users size is " + users.size());
        return users;
    }

    // Method for "ADMIN" cabinet page
    @Override
    public List<UserEntityDto> findUsersByUsernames(String query) {
        List<UserEntityDto> users = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("user-service")
                        .path("/users/searchUsersByQuery")
                        .queryParam("query",query)
                        .build())
                .retrieve()
                .bodyToFlux(UserEntityDto.class)
                .collectList()
                .onErrorReturn(Collections.emptyList())
                .block();
        return users;
    }

    @Override
    public UserEntityDto findUserById(Long userId) {
        log.info("Finding user with id " + userId);
        return  webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("user-service")
                        .path("/users/get")
                        .queryParam("userId" ,userId)
                        .build())
                .retrieve()
                .bodyToMono(UserEntityDto.class)
                .block();
    }

}
