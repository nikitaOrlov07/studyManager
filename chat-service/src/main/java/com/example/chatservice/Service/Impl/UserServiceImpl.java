package com.example.chatservice.Service.Impl;

import com.example.chatservice.Service.UserService;
import com.example.chatservice.conf.webClientConf.WebClientConfig;
import lombok.RequiredArgsConstructor;
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
    public Boolean changeChatIds(List<Long> userIds, Long id, String operationType) { // operation type can be delete or add
       Boolean result = webClientBuilder.build()
               .post()
               .uri(uriBuilder -> uriBuilder
                       .scheme("http")
                       .host("user-service")
                       .path("/users/chats/addChatsIds/"+operationType)
                       .queryParam("usersIds",userIds)
                       .queryParam("chatId", id)
                       .build())
               .retrieve()
               .bodyToMono(Boolean.class)
               .block();
       if(result)
           log.info("saveChatIds method is working correctly");
       if(!result)
           log.error("Error in saveChatIds");

       return result;
    }
}
