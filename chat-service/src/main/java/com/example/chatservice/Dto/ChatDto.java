package com.example.chatservice.Dto;

import lombok.Builder;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ChatDto {

    private Long id;
    private List<Long> participantsIds = new ArrayList<Long>();
    private List<MessageDto> messages = new ArrayList<>();
    private Long courseId; // will be not null if this is course chat
}
