package com.example.courseservice.Dto.Chat;

import com.example.courseservice.Dto.UserEntityDto;
import com.example.courseservice.Model.Course;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Chat {
    private Long id;
    private List<UserEntityDto> participants= new ArrayList<UserEntityDto>();
    private List<Message> messages = new ArrayList<Message>();

    @OneToOne
    private Course course;
}
