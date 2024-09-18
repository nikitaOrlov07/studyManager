package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.Chat.ChatDto;
import com.example.mainservice.Service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    private WebClient.Builder webClientBuilder;

    @Autowired
    public ChatServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public ChatDto getChatById(Long chatId) {
        ChatDto chatDto =  webClientBuilder.build()
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
        if(chatDto == null)
        {
            log.error("Error in http request for chatD");
        }
       return  chatDto;
    }
}
