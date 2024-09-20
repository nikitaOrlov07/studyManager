package com.example.chatservice.Mapper;

import com.example.chatservice.Dto.ChatDto;
import com.example.chatservice.Dto.MessageDto;
import com.example.chatservice.Model.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatMapper {
    public static ChatDto chatToChatDto(Chat chat)
    {
        List<MessageDto> messages = new ArrayList<MessageDto>();
        if(chat.getMessages() != null && !chat.getMessages().isEmpty())
        {
          messages = chat.getMessages().stream().map(MessageMapper::messageToMessageDto).collect(Collectors.toList());
        }
        ChatDto chatDto = ChatDto.builder()
                .id(chat.getId())
                .courseId(chat.getCourseId())
                .messages(messages)
                .participantsIds(chat.getParticipantsIds())
                .build();

        return chatDto;
    }
}
