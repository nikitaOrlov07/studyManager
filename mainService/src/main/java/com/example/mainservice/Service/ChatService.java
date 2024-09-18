package com.example.mainservice.Service;

import com.example.mainservice.Dto.Chat.ChatDto;

public interface ChatService {
    ChatDto getChatById(Long chatId);
}
