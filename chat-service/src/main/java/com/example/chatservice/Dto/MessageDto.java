package com.example.chatservice.Dto;

import com.example.chatservice.conf.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;
    private String text;
    private String author;
    private String pubDate;
    private MessageType type;
    private Long chatId;
}
