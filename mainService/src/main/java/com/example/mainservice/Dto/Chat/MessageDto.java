package com.example.mainservice.Dto.Chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {

    private Long id;
    private String text;
    private String author;
    private String pubDate;
    private MessageType type;
    private ChatDto chat;

}
