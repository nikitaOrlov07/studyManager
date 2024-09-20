package com.example.chatservice.Controller;

import com.example.chatservice.Dto.ChatDto;
import com.example.chatservice.Dto.UserEntityDto;
import com.example.chatservice.Mapper.ChatMapper;
import com.example.chatservice.Model.Chat;
import com.example.chatservice.Model.Message;
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
    public Long saveChat(@RequestParam(value = "courseId",required = false) Long courseId , @RequestParam(value = "currentId",required = false) Long currentId) // save chat for course
    {
        Long chatId =  chatService.saveChat(courseId,currentId); // return true if chat was successfully saved
        log.info("For course with id : "+ courseId + " was created chat with id: "+ chatId);
        return chatId;
    }
    @GetMapping("/findChatById")
    public ChatDto findChatById(@RequestParam("chatId") Long chatId)
    {
        try {
            return ChatMapper.chatToChatDto(chatService.getChatById(chatId));
        }
        catch (Exception e)
        {
            log.error("Error :"+e.getMessage());
            return null;
        }
    }
    @PostMapping("/delete/{chatId}")
    public Boolean deleteChatById(@PathVariable Long chatId){
        log.info("Chat service \"deleteChatById\" controller method is working");
        try {
            Boolean result = chatService.deleteChatById(chatId);
            if (result)
                log.info("Chat was deleted successfully");
            else
                log.error("Error occurred while deleting a chat");

            return result;
        }
        catch (Exception e)
        {
            log.error("Error :"+e.getMessage());
            return false;
        }
    }



}
