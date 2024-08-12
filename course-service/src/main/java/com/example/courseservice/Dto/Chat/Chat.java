package com.example.courseservice.Dto.Chat;

import com.example.courseservice.Dto.UserEntity;
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
    private List<UserEntity> participants= new ArrayList<UserEntity>();
    private List<Message> messages = new ArrayList<Message>();

    @OneToOne
    private Course course;
}
