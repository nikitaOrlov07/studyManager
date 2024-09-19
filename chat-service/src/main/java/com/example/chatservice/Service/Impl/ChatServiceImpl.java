package com.example.chatservice.Service.Impl;

import com.example.chatservice.Model.Chat;
import com.example.chatservice.Repository.ChatRepository;
import com.example.chatservice.Service.ChatService;
import com.example.chatservice.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private ChatRepository chatRepository;
    private UserService userService;
    private WebClient.Builder webClientBuilder;
    private ObjectMapper objectMapper;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository,UserService userService , WebClient.Builder webClientBuilder,ObjectMapper objectMapper) {
        this.chatRepository = chatRepository;
        this.userService = userService;
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    @Override
    public Chat findOrCreateChat(Long secondId, Long currentId) {
        List<Chat> existingChats = chatRepository.findByParticipantsIdsContains(currentId);
        // Find existing chat with second user
        for (Chat chat : existingChats) {
            if (chat.getParticipantsIds().contains(secondId)) {
                log.info("Was returned existing chat");
                return chat;
            }
        }
        // Create new chat
        Chat chat = Chat.builder()
                .participantsIds(Arrays.asList(currentId,secondId))
                .build();
        // Create a new chat
        Chat savedChat = chatRepository.save(chat);
        if(savedChat == null || !savedChat.getParticipantsIds().contains(currentId) || !savedChat.getParticipantsIds().contains(secondId))
        {
            return null;
        }
        // Request to userService to save chat id
        userService.saveChatIds(new ArrayList<>(Arrays.asList(currentId,secondId)) , chat.getId(),"add");
        return  chat;
    }

    @Override
    public Long  saveChat(Long courseId, Long currentId) {
        Chat chat = Chat.builder()
                .participantsIds(new ArrayList<>(Arrays.asList(currentId)))
                .courseId(courseId)
                .build();

        Chat savedChat = chatRepository.save(chat);

        // Http request to userService to save chatId
        userService.saveChatIds(Arrays.asList(currentId),chat.getId(),"add");
        return savedChat.getId();
    }
    @Override
    public Chat getChatById(Long chatId) throws Exception {
        return  chatRepository.findById(chatId).orElseThrow(() -> new Exception("Chat not found"));
    }
}
