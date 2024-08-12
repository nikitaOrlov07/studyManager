package com.example.courseservice.Dto;

import com.example.courseservice.Dto.Chat.Chat;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Model.Homework;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // created courses
    private List<Course> courses = new ArrayList<>();

    private List<Course> currentCourses  = new ArrayList<>();

    List<Chat> chats = new ArrayList<>();

    // homework
    private List<Homework>  homeworks = new ArrayList<>();

    // completed homeworks
    private List<StudentHomeworkAttachment> completedHomeworks = new ArrayList<>();

    // created homeworks
    private List<Homework> createdHomeworks = new ArrayList<>();
}
