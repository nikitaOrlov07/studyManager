package com.example.mainservice.Controller;

import com.example.mainservice.Dto.Chat.DeleteMessageRequest;
import com.example.mainservice.Dto.Chat.MessageDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.ChatService;
import com.example.mainservice.Service.MessageService;
import com.example.mainservice.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.mainservice.Dto.Chat.ChatDto;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.mainservice.Dto.Chat.MessageType;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/chats")
@Slf4j
public class ChatController {
    private UserService userService;
    private ChatService chatService;
    private MessageService messageService;
    @Autowired
    public ChatController(UserService userService, ChatService chatService,MessageService messageService) {
        this.userService = userService;
        this.chatService = chatService;
        this.messageService=messageService;
    }


    @GetMapping("/{chatId}")
    public String getChatPage(@PathVariable Long chatId,
                              Model model) throws JsonProcessingException {
        UserEntityDto user = userService.findUserByUsername(SecurityUtil.getSessionUser());
        // Get chat
        ChatDto chat = chatService.getChatById(chatId);
        if(user == null || !chat.getParticipantsIds().contains(user.getId()))
        {
            log.error("the user cannot access the chat with the id: " + chatId);
            return "redirect:/home?notAllowed";
        }
        List<UserEntityDto> participants = userService.findUsersByIds(chat.getParticipantsIds());
        log.debug("Chats first user: "+ participants.get(0).getUsername());

        model.addAttribute("messagesJson", new ObjectMapper().writeValueAsString(chat.getMessages()));
        model.addAttribute("participants",participants.remove(user));
        model.addAttribute("chat", chat);
        model.addAttribute("user",user);

        return "chat";
    }
    @GetMapping("/findOrCreateChat")
    public String findOrCreateChat(@RequestParam(required = false) Long secondUserId)
    {
        log.info("Main Service \"findOrCreateChat\" controller method is working");
        UserEntityDto currentUser = userService.findUserByUsername(SecurityUtil.getSessionUser());
        if (currentUser == null)
        {
            log.error("unauthorised user try reach chat page");
            return "redirect:/home?notAllowed";
        }
        Long chatId = chatService.findOrCreateChat(secondUserId, currentUser.getId());
        return "redirect:/chats/"+chatId;
    }



    // Messages
    @MessageMapping("/chats/{chatId}/sendMessage") // REQUEST MAPPING WORK ONLY FOR HTTP METHODS
    @SendTo("/topic/chat/{chatId}")// The reply will be sent to the channel
    public MessageDto sendMessage(@DestinationVariable Long chatId, @Payload MessageDto messageDto,
                                  SimpMessageHeaderAccessor headerAccessor)
    {
        log.info("Controller method \"sendMessage\" controller method is working");
        String username = SecurityUtil.getSessionUser(headerAccessor.getUser());
        if (username == null) {
            throw new IllegalStateException("User not authenticated");
        }
        UserEntityDto userEntityDto = userService.findUserByUsername(username);
        if(!userEntityDto.getChatsIds().contains(chatId))
        {
            throw new IllegalStateException("User not");
        }
        messageDto.setAuthor(username);
        messageDto.setPubDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        messageDto.setChatId(chatId);
        messageService.saveMessage(messageDto);

        return messageDto;
    }
    @MessageMapping("/chats/{chatId}/addUser")
    @SendTo("/topic/chat/{chatId}")
    public MessageDto addUser(@DestinationVariable Long chatId, @Payload MessageDto message,
                              SimpMessageHeaderAccessor headerAccessor) {
        String username = message.getAuthor();
        headerAccessor.getSessionAttributes().put("username", username);

        UserEntityDto currentUser = userService.findUserByUsername(username);
        ChatDto chat = chatService.getChatById(chatId);
        if (chat == null) {
            throw new IllegalStateException("Chat with id " + chatId + " does not exist");
        }

        // Check if the user is already in the chat
        if (!chat.getParticipantsIds().contains(currentUser.getId())) {
            // Find the other user in the chat
            Long otherUserId = chat.getParticipantsIds().stream()
                    .filter(id -> !id.equals(currentUser.getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No other user found in chat"));

            List<UserEntityDto> users = userService.findUsersByIds(Arrays.asList(otherUserId));
            if (users.isEmpty()) {
                throw new IllegalStateException("Other user not found");
            }
            UserEntityDto otherUser = users.get(0);

            chat = chatService.getChatById(chatService.findOrCreateChat(currentUser.getId(), otherUser.getId()));

            // if a new chat was created -> update id
            if (!chat.getId().equals(chatId)) {
                message.setChatId(chat.getId());
            }
            return message;
        } else {
            // user is already a member of the chat
            return null;
        }
    }
    @MessageMapping("/chats/{chatId}/delete")
    @SendTo("/topic/chat/{chatId}")
    public MessageDto deleteChat(@DestinationVariable Long chatId, Principal principal) {
        String username = SecurityUtil.getSessionUser(principal);
        UserEntityDto user = userService.findUserByUsername(username);
        ChatDto chat = chatService.getChatById(chatId);
        log.info("Delete chat controller method is working");

        // implement "List.contains()" logic -> if i use regular contains method -> won`t working
        boolean userInChat = chat.getParticipantsIds().contains(user.getId());

        boolean chatInUserChats = user.getChatsIds().contains(chatId);

        if (chat != null && userInChat && chatInUserChats) {
            Boolean result = chatService.deleteChat(chat);
            if(result) {
                log.info("Chat was deleted successfully");
                return MessageDto.builder()
                        .type(MessageType.CHAT_DELETED)
                        .author(username)
                        .text("Chat deleted")
                        .build();
            }
            if(!result)
            {
                log.error("Error in deleting chat");
                return  null;
            }

        }
        else {
            log.warn("Error deleting chat. User in chat: {}, Chat in user chats: {}", userInChat, chatInUserChats);
            return null;
        }
        return null;
    }
    @MessageMapping("/chats/{chatId}/deleteMessage")
    @SendTo("/topic/chat/{chatId}")
    @Transactional
    public MessageDto deleteMessage(@DestinationVariable Long chatId, @Payload DeleteMessageRequest request,
                                 SimpMessageHeaderAccessor headerAccessor) {
        log.info("Delete message controller in working");
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        UserEntityDto user = userService.findUserByUsername(username);
        MessageDto message = messageService.findById(request.getMessageId());
        ChatDto chat = chatService.getChatById(chatId);
        if (message != null && chat != null && message.getAuthor().equals(user.getUsername())) {
            messageService.deleteMessage(message, user);
            log.info("Message deleted successfully");
            return MessageDto.builder()
                    .type(MessageType.DELETE)
                    .author(username)
                    .id(request.getMessageId())
                    .text(request.getMessageId().toString())
                    .build();
        } else {
            log.warn("Could not delete message. User mismatch or message/chat not found.");
            return null;
        }
    }



}

