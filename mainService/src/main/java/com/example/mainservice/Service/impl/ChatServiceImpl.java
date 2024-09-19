package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.Chat.ChatDto;
import com.example.mainservice.Service.ChatService;
import com.example.mainservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    private WebClient.Builder webClientBuilder;
    private UserService userService;
    @Autowired
    public ChatServiceImpl(WebClient.Builder webClientBuilder ,  UserService userService) {
        this.webClientBuilder = webClientBuilder;
        this.userService = userService;
    }

    @Override
    public ChatDto getChatById(Long chatId) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("chat-service")
                        .path("/chats/findChatById")
                        .queryParam("chatId", chatId)
                        .build())
                .retrieve()
                .bodyToMono(ChatDto.class)
                .block();
    }

    @Override
    public Long findOrCreateChat(Long secondUserId, Long currentUserId) {
        log.info("Current user id: "+ currentUserId);
        log.info("Second user id: "+ secondUserId);
        ChatDto chatDto = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("chat-service")
                        .path("/chats/findOrCreate")
                        .queryParam("secondId",secondUserId)
                        .queryParam("currentId",currentUserId)
                        .build())
                .retrieve()
                .bodyToMono(ChatDto.class)
                .block();
        log.info("Chat id: "+ chatDto.getId());
        return chatDto.getId();
    }


}
