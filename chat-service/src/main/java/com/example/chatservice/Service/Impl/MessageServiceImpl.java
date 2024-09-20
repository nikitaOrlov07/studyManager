package com.example.chatservice.Service.Impl;

import com.example.chatservice.Dto.MessageDto;
import com.example.chatservice.Mapper.MessageMapper;
import com.example.chatservice.Model.Chat;
import com.example.chatservice.Model.Message;
import com.example.chatservice.Repository.ChatRepository;
import com.example.chatservice.Repository.MessageRepository;
import com.example.chatservice.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper mapper;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;

    @Override
    public String saveMessage(MessageDto messageDto) throws Exception {
        // Map MessageDto to Message
        Message message = mapper.messageDtoToMessage(messageDto);
        if(message == null)
        {
            return "Error while using message mapper";
        }
        Message savedMessage = messageRepository.save(message);
        if(savedMessage == null || savedMessage.getText() != message.getText())
        {
            return "Error while saving";
        }
        return "Message was successfully saved";
    }

    @Override
    public MessageDto findMessageDtoById(Long messageId) {
        Message message =  messageRepository.findById(messageId).orElseThrow(() ->new IllegalStateException("Message not found"));
        return  MessageMapper.messageToMessageDto(message);
    }

    @Override
    public Boolean deleteMessage(Long messageId, Long userId) throws Exception {
        // Find Message Entity
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new Exception("Message not found"));

        // Find chat Entity
        Chat chat = message.getChat();
        if(chat == null || !chat.getMessages().contains(message))
        {
            return false;
        }
        chat.getMessages().remove(message);

        // Save changes
        chatRepository.save(chat);
        messageRepository.delete(message);
        return true;
    }
}
