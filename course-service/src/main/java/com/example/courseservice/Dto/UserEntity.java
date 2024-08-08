package com.example.courseservice.Dto;

import com.example.courseservice.Dto.Chat;
import com.example.courseservice.Model.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CollectionType;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserEntity {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String age;
    private String town;
    private Long phoneNumber;
    private String role;

    private List<Course> currentCourses  = new ArrayList<>();
    List<Chat> chats = new ArrayList<>();
}
