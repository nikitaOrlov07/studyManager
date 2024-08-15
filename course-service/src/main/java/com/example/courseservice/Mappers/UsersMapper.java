package com.example.courseservice.Mappers;

import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import com.example.courseservice.Dto.UserEntity.UserEntityResponse;
import com.example.courseservice.Dto.UserEntityDto;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import com.example.courseservice.Service.CourseService;
import com.example.courseservice.Service.HomeworkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.courseservice.Dto.Homework.HomeworkResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsersMapper {


    private static CourseService courseService;

    private static HomeworkService homeworkService;

    @Autowired
    public UsersMapper(CourseService courseService, HomeworkService homeworkService) {
        UsersMapper.courseService = courseService;
        UsersMapper.homeworkService = homeworkService;
    }

    // Convert UserEntityDto to UserEntityResponse
   /* public static UserEntityResponse dtoToResponse(UserEntityDto dto) {
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
               //  .chatsIds(dto.getChats().stream().map(Chat::getId).collect(Collectors.toList()))
                .completedHomeworksIds(dto.getCompletedHomeworks().stream().map(StudentHomeworkAttachment::getId).collect(Collectors.toList()))
               //  .createdHomeworksIds(dto.getCreatedHomeworks().stream().map(Homework::getId).collect(Collectors.toList()))
                .build();
    }

    */

    // Convert UserEntityResponse to UserEntityDto
    public static UserEntityDto responseToDto(UserEntityResponse response) {
        List<Course> courses = new ArrayList<>();
        List<Course> participatingCourses = new ArrayList<>();
        List<StudentHomeworkAttachmentDto> completedHomeworks = new ArrayList<>();
        List<HomeworkResponse> createdHomeworks = new ArrayList<>();

        log.info("responseToDto user mapper is  working");
        if(response.getCreatedCoursesIds() != null)
        {courses = courseService.getCourseByIds(response.getCreatedCoursesIds());}

        if(response.getParticipatingCourses() != null) {
            participatingCourses = courseService.getCourseByIds(response.getParticipatingCourses());
        }
        /* List<Chat> chats = response.getChatsIds().stream()
                .map(id -> fetchChatById(id)) // Replace with actual method to fetch Chat
                .collect(Collectors.toList()); */

        if(response.getCompletedHomeworksIds() != null)
        {completedHomeworks = homeworkService.findHomeworkAttachmentsByIds(response.getCompletedHomeworksIds());}
          //
        if(response.getCreatedHomeworksIds() != null) {
           createdHomeworks = homeworkService.getCreatedHomeworksByIds(response.getCreatedHomeworksIds());
        }
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
                // .chats(chats)
                .homeworks(createdHomeworks)
                .completedHomeworks(completedHomeworks)
                .createdHomeworks(createdHomeworks)
                .build();
    }
}
