package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.Chat.ChatDto;
import com.example.mainservice.Service.ChatService;
import com.example.mainservice.Service.TokenService;
import com.example.mainservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    private WebClient.Builder webClientBuilder;
    private UserService userService;
    private TokenService tokenService;
    @Autowired
    public ChatServiceImpl(WebClient.Builder webClientBuilder ,  UserService userService , TokenService tokenService) {
        this.webClientBuilder = webClientBuilder;
        this.userService = userService;
        this.tokenService= tokenService;
    }
    private WebClient.RequestHeadersSpec<?> addAuthHeader(WebClient.RequestHeadersSpec<?> requestSpec, String username) throws Exception {
        String token = null;
        try {
            token = tokenService.findTokenByUsername(username);
            log.info("Found token: "+ token);
        }
        catch (Exception e)
        {
            log.error("Error:"+ e.getMessage());
            return null;
        }
        return requestSpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
    @Override
    public ChatDto getChatById(Long chatId, String username) throws Exception {
        return addAuthHeader(webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("chat-service")
                        .path("/chats/findChatById")
                        .queryParam("chatId", chatId)
                        .build()), username)
                .retrieve()
                .bodyToMono(ChatDto.class)
                .block();
    }

    @Override
    public Long findOrCreateChat(Long secondUserId, String currentUsername) throws Exception {
        log.info("Current user username: " + currentUsername);
        log.info("Second user id: " + secondUserId);
        ChatDto chatDto = addAuthHeader(webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("chat-service")
                        .path("/chats/findOrCreate")
                        .queryParam("secondId", secondUserId)
                        .queryParam("currentUsername", currentUsername)
                        .build()), currentUsername)
                .retrieve()
                .bodyToMono(ChatDto.class)
                .block();
        log.info("Chat id: " + chatDto.getId());
        return chatDto.getId();
    }

    @Override
    public Boolean deleteChat(Long chatId, String username) throws Exception {
        log.info("Attempting to delete chat with id: {} for user: {}", chatId, username);

        Boolean result = addAuthHeader(webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("chat-service")
                        .path("/chats/delete/" + chatId)
                        .build()), username)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (result != null && result) {
            log.info("Chat with id: {} successfully deleted for user: {}", chatId, username);
        } else {
            log.warn("Failed to delete chat with id: {} for user: {}", chatId, username);
        }

        return result;
    }


}
