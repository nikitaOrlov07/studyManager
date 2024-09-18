package com.example.mainservice.Controller;

import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.ChatService;
import com.example.mainservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.mainservice.Dto.Chat.ChatDto;

@Controller
@RequestMapping("/chats")
@Slf4j
public class ChatController {
    private UserService userService;
    private ChatService chatService;

    @GetMapping("/chat/{chatId}")
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

        // Get chat participants
        List<>


        model.addAttribute("participants",participants)
        model.addAttribute("chat", chat);
    }

}

