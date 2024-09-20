package com.example.mainservice.Service;

import com.example.mainservice.Dto.Chat.MessageDto;
import com.example.mainservice.Dto.User.UserEntityDto;

public interface MessageService {

    String saveMessage(MessageDto messageDto);

    MessageDto findById(Long messageId);

    Boolean deleteMessage(MessageDto message, UserEntityDto user);
}
