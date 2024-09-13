package com.example.courseservice.Service.impl;

import com.example.courseservice.Dto.UserEntity.UserEntityResponse;
import com.example.courseservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public List<UserEntityResponse> findUsersById(List<Long> userIds) {
        List<UserEntityResponse> result = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("user-service")
                        .path("/users/findUsersByIds")
                        .queryParam("usersIds", userIds)
                        .build())
                .retrieve()
                .bodyToFlux(UserEntityResponse.class)
                .collectList()
                .block();
        return result;
    }


    @Override
    public void updateUserItems(String type, String action, Long id, Long userId) {
        webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("user-service")
                        .path("/users/{action}/{type}/{id}")
                        .queryParam("userId", userId)
                        .build(action, type, id))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public UserEntityResponse findUsersByUsername(String username) {
        log.info("MainService \"findUserByUsername \" service method is working");
        UserEntityResponse userEntityResponse = webClientBuilder.build()
                .get()
                .uri("http://user-service/users/"+username)
                .retrieve()
                .bodyToMono(UserEntityResponse.class)
                .block();

        return userEntityResponse;
    }

}
