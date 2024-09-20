package com.example.chatservice.Service;

import com.example.chatservice.Dto.MessageDto;

public interface MessageService {

    String saveMessage(MessageDto messageDto) throws Exception;

    MessageDto findMessageDtoById(Long messageId);

    Boolean deleteMessage(Long messageId, Long userId) throws Exception;
}
