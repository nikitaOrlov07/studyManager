package com.example.courseservice.Mappers;

import com.example.courseservice.Dto.UserEntity.UserEntityResponse;
import com.example.courseservice.Dto.UserEntityDto;
import com.example.courseservice.Model.Course;

import java.util.stream.Collectors;

public class UsersMapper {
    // Convert UserEntityDto to UserEntityResponse
    public static UserEntityResponse dtoToResponse(UserEntityDto dto) {
        return UserEntityResponse.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .age(dto.getAge())
                .town(dto.getTown())
                .phoneNumber(dto.getPhoneNumber())
                .role(dto.getRole())
                .createdCoursesIds(dto.getCourses().stream().map(Course::getId).collect(Collectors.toList()))
                .participatingCourses(dto.getParticipatingCourses().stream().map(Course::getId).collect(Collectors.toList()))
                .chatsIds(dto.getChats().stream().map(Chat::getId).collect(Collectors.toList()))
                .completedHomeworksIds(dto.getCompletedHomeworks().stream().map(StudentHomeworkAttachment::getId).collect(Collectors.toList()))
                .createdHomeworksIds(dto.getCreatedHomeworks().stream().map(Homework::getId).collect(Collectors.toList()))
                .build();
    }

    // Convert UserEntityResponse to UserEntityDto
    public static UserEntityDto responseToDto(UserEntityResponse response) {
        // Assuming you have some methods to fetch detailed entities by their IDs
        List<Course> courses = response.getCreatedCoursesIds().stream()
                .map(id -> fetchCourseById(id)) // Replace with actual method to fetch Course
                .collect(Collectors.toList());

        List<Course> participatingCourses = response.getParticipatingCourses().stream()
                .map(id -> fetchCourseById(id)) // Replace with actual method to fetch Course
                .collect(Collectors.toList());

        List<Chat> chats = response.getChatsIds().stream()
                .map(id -> fetchChatById(id)) // Replace with actual method to fetch Chat
                .collect(Collectors.toList());

        List<StudentHomeworkAttachment> completedHomeworks = response.getCompletedHomeworksIds().stream()
                .map(id -> fetchStudentHomeworkAttachmentById(id)) // Replace with actual method to fetch StudentHomeworkAttachment
                .collect(Collectors.toList());

        List<Homework> createdHomeworks = response.getCreatedHomeworksIds().stream()
                .map(id -> fetchHomeworkById(id)) // Replace with actual method to fetch Homework
                .collect(Collectors.toList());

        return UserEntityDto.builder()
                .id(response.getId())
                .username(response.getUsername())
                .email(response.getEmail())
                .password(response.getPassword())
                .age(response.getAge())
                .town(response.getTown())
                .phoneNumber(response.getPhoneNumber())
                .role(response.getRole())
                .courses(courses)
                .participatingCourses(participatingCourses)
                .chats(chats)
                .homeworks(createdHomeworks) // Assuming homeworks here refers to created homeworks
                .completedHomeworks(completedHomeworks)
                .createdHomeworks(createdHomeworks)
                .build();
    }
}
