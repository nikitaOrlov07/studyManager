package com.example.courseservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;
    private String text;
    private String author;
    private String pubDate;
    private MessageType type;
    private Chat chat;
    private UserEntity user;
}
