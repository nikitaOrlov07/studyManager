package com.example.chatservice.Service;

import com.example.chatservice.Model.Chat;

public interface ChatService {
    Chat findOrCreateChat(Long secondId, Long currentId);

    Long saveChat(Long courseId, Long currentId);

    Chat getChatById(Long chatId) throws Exception;

    Boolean deleteChatById(Long chatId) throws Exception;
}
