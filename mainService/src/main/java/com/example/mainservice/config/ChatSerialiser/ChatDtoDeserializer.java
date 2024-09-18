package com.example.mainservice.config.ChatSerialiser;

import com.example.mainservice.Dto.Chat.ChatDto;
import com.example.mainservice.Dto.Chat.MessageDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Service.UserService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatDtoDeserializer extends JsonDeserializer<ChatDto> {

    private final UserService userService;

    public ChatDtoDeserializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ChatDto deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = mapper.readTree(jp);
        
        ChatDto chatDto = new ChatDto();
        
        chatDto.setId(node.get("id").asLong());
        chatDto.setCourseId(node.get("courseId").asLong());
        
        List<UserEntityDto> participants = new ArrayList<>();
        JsonNode participantsNode = node.get("participantsIds");
        if (participantsNode.isArray()) {
            for (JsonNode participantId : participantsNode) {
                Long userId = participantId.asLong();
                UserEntityDto userDto = userService.findUsersByIds(Arrays.asList(userId)).get(0);
                if (userDto != null) {
                    participants.add(userDto);
                }
            }
        }
        chatDto.setParticipants(participants);
        
        // Десериализация сообщений, если они есть
        List<MessageDto> messages = new ArrayList<>();
        JsonNode messagesNode = node.get("messages");
        if (messagesNode != null && messagesNode.isArray()) {
            for (JsonNode messageNode : messagesNode) {
                MessageDto messageDto = mapper.treeToValue(messageNode, MessageDto.class);
                messages.add(messageDto);
            }
        }
        chatDto.setMessages(messages);
        
        return chatDto;
    }
}