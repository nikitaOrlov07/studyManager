package com.example.chatservice.Controller;

import com.example.chatservice.Dto.UserEntityDto;
import com.example.chatservice.Model.Chat;
import com.example.chatservice.Service.ChatService;
import com.example.chatservice.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/findOrCreate")
    public Chat findOrCreate(@RequestParam("secondId") Long secondId, @RequestParam("currentId") Long currentId)
    {
        Chat chat = chatService.findOrCreateChat(secondId, currentId);
        return chat;
    }
    @PostMapping("/saveChat") // save chat for course
    public Boolean saveChat(@RequestParam("courseId") Long courseId , @RequestParam("currentId") Long currentId) // save chat for course
    {
        return chatService.saveChat(courseId,currentId); // return true if chat was successfully saved
    }
    @GetMapping("/findChatById")
    public Chat findChatById(@RequestParam("chatId") Long chatId)
    {
        try {
            return chatService.getChatById(chatId);
        }
        catch (Exception e)
        {
            log.error("Error :"+e.getMessage());
            return null;
        }
    }


}
