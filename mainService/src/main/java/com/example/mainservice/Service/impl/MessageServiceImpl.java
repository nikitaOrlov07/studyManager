package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.Chat.MessageDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class MessageServiceImpl  implements MessageService {

    @Autowired
    private WebClient.Builder webClientBuilder;
    @Override
    public String saveMessage(MessageDto messageDto) {
     String result = webClientBuilder.build()
             .post()
             .uri(uriBuilder -> uriBuilder
                     .scheme("http")
                     .host("chat-service")
                     .path("/messages/save")
                     .build())
             .bodyValue(messageDto)
             .retrieve()
             .bodyToMono(String.class)
             .block();
     return result;
    }

    @Override
    public MessageDto findById(Long messageId) {
        MessageDto messageDto =
                webClientBuilder.build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("http")
                                .host("chat-service")
                                .path("/messages")
                                .queryParam("messageId",messageId)
                                .build())
                        .retrieve()
                        .bodyToMono(MessageDto.class)
                        .block();
        return messageDto;
    }

    @Override
    public Boolean deleteMessage(MessageDto message, UserEntityDto user) {
        Boolean result = webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("chat-service")
                        .path("/messages/delete")
                        .queryParam("messageId",message.getId())
                        .queryParam("userId",user.getId())
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        return result;
    }
}
