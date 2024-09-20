package com.example.chatservice.Mapper;

import com.example.chatservice.Dto.MessageDto;
import com.example.chatservice.Model.Chat;
import com.example.chatservice.Model.Message;
import com.example.chatservice.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper {
    @Autowired
    public ChatService chatService;

    public static MessageDto messageToMessageDto(Message message)
    {
        MessageDto messageDto = MessageDto.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .author(message.getAuthor())
                .pubDate(message.getPubDate())
                .text(message.getText())
                .type(message.getType())
                .build();
        return messageDto;
    }
    public  Message messageDtoToMessage(MessageDto messageDto) throws Exception {
        Chat chat = chatService.getChatById(messageDto.getChatId());
        Message message = Message.builder()
                .chat(chat)
                .author(messageDto.getAuthor())
                .pubDate(messageDto.getPubDate())
                .text(messageDto.getText())
                .type(messageDto.getType())
                .build();
        return message;
    }
}
