package com.example.chatservice.Controller;

import com.example.chatservice.Dto.MessageDto;
import com.example.chatservice.Mapper.MessageMapper;
import com.example.chatservice.Model.Message;
import com.example.chatservice.Service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    public MessageDto getMessageById(@RequestParam Long messageId)
    {
        return  messageService.findMessageDtoById(messageId);
    }

    @PostMapping("/save")
    public String saveMessage(@RequestBody MessageDto messageDto) throws Exception {
        log.info("\"saveMessage\" controller method is working");
        String result = messageService.saveMessage(messageDto);
        if("Message was successfully saved".equals(result))
        {
            log.info(result);
            return result;
        }
        else
        {
            log.error(result);
            return result;
        }
    }

    @PostMapping("/delete")
    public Boolean deleteMessage(@RequestParam("messageId") Long messageId,
                                @RequestParam("userId") Long userId)
    {

        try {
            Boolean result = messageService.deleteMessage(messageId, userId);
            return result;
        }
        catch (Exception e)
        {
            log.error("While trying to delete message with id {} -> occurred Error: {}",messageId,e.getMessage());
            return false;
        }
    }

}
