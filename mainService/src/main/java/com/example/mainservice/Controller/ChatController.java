package com.example.mainservice.Controller;

import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.ChatService;
import com.example.mainservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.mainservice.Dto.Chat.ChatDto;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/chats")
@Slf4j
public class ChatController {
    private UserService userService;
    private ChatService chatService;
    @Autowired
    public ChatController(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }


    @GetMapping("/{chatId}")
    public String getChatPage(@PathVariable Long chatId,
                              Model model)
    {
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

}

