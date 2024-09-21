package com.example.mainservice.Service;

import com.example.mainservice.Dto.Chat.ChatDto;
import com.example.mainservice.Dto.Chat.MessageDto;

public interface ChatService {
    ChatDto getChatById(Long chatId, String username) throws Exception;

    Long findOrCreateChat(Long secondUserId, String currentUsername) throws Exception;

    Boolean deleteChat(Long chatId, String username) throws Exception;
}
