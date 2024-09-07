package com.example.courseservice.Dto.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntityResponse {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String age;
    private String town;
    private String phoneNumber;
    private String role;
    private List<Long> createdCoursesIds = new ArrayList<>();
    private List<Long> participatingCourses = new ArrayList<>();
    private List<Long> chatsIds = new ArrayList<>();
    private List<Long> completedHomeworksIds = new ArrayList<>();
    private List<Long> createdHomeworksIds = new ArrayList<>();
}
