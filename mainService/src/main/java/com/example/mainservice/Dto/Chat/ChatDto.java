package com.example.mainservice.Dto.Chat;

import com.example.mainservice.Dto.User.UserEntityDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatDto {

    private Long id;
    private List<Long> participantsIds = new ArrayList<Long>();
    private List<MessageDto> messages = new ArrayList<>();
    private Long courseId; // will be not null if this is course chat
}
