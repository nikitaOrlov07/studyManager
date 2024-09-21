package com.example.chatservice.Controller;

import com.example.chatservice.Dto.ChatDto;
import com.example.chatservice.Dto.UserEntityDto;
import com.example.chatservice.Mapper.ChatMapper;
import com.example.chatservice.Model.Chat;
import com.example.chatservice.Model.Message;
import com.example.chatservice.Service.ChatService;
import com.example.chatservice.Service.UserService;
import com.example.chatservice.conf.JwtConfiguration.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/chats")
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;

    @Autowired
    public ChatController(ChatService chatService, JwtUtil jwtUtil) {
        this.chatService = chatService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/findOrCreate")
    public ResponseEntity<Chat> findOrCreate(@RequestParam("secondId") Long secondId, Authentication authentication) {
        UserEntityDto currentUser = (UserEntityDto) authentication.getPrincipal();
        Chat chat = chatService.findOrCreateChat(secondId, currentUser.getId());
        return ResponseEntity.ok(chat);
    }

    @PostMapping("/saveChat")
    public ResponseEntity<Long> saveChat(@RequestParam(value = "courseId", required = false) Long courseId,
                                         Authentication authentication) {
        UserEntityDto currentUser = (UserEntityDto) authentication.getPrincipal();
        Long chatId = chatService.saveChat(courseId, currentUser.getId());
        log.info("For course with id: {} was created chat with id: {}", courseId, chatId);
        return ResponseEntity.ok(chatId);
    }

    @GetMapping("/findChatById")
    public ResponseEntity<ChatDto> findChatById(@RequestParam("chatId") Long chatId, Authentication authentication) {
        try {
            UserEntityDto currentUser = (UserEntityDto) authentication.getPrincipal();
            ChatDto chatDto = ChatMapper.chatToChatDto(chatService.getChatById(chatId));
            return ResponseEntity.ok(chatDto);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Boolean> deleteChatById(@PathVariable Long chatId, Authentication authentication) {
        log.info("Chat service \"deleteChatById\" controller method is working");
        try {
            UserEntityDto currentUser = (UserEntityDto) authentication.getPrincipal();
            Boolean result = chatService.deleteChatById(chatId);
            if (result) {
                log.info("Chat was deleted successfully");
            } else {
                log.error("Error occurred while deleting a chat");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
