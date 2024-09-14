package com.example.courseservice.Dto.UserEntity;

import com.example.courseservice.Dto.Chat.Chat;
import com.example.courseservice.Dto.Homework.HomeworkResponse;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import com.example.courseservice.Model.Course;
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
public class UserEntityDto {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String age;
    private String town;
    private String phoneNumber;
    private String role;
    private String registrationDate;

    // created courses
    private List<Course> courses = new ArrayList<>();

    //     // User is participant in this courses
    private List<Course> participatingCourses  = new ArrayList<>();

    List<Chat> chats = new ArrayList<>();

    // homework
    private List<HomeworkResponse>  homeworks = new ArrayList<>();

    // completed homeworks
    private List<StudentHomeworkAttachmentDto> completedHomeworks = new ArrayList<>();

    // created homeworks
    private List<HomeworkResponse> createdHomeworks = new ArrayList<>();
}
