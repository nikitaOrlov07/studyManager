package com.example.userservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String age;
    private String town;
    private String phoneNumber;
    private String registrationDate;

    // created courses
    @ElementCollection
    private List<Long> createdCoursesIds = new ArrayList<>();

    // The user is a participant in these courses
    @ElementCollection
    private List<Long> participatingCourses = new ArrayList<>();

    @ElementCollection
    private List<Long> chatsIds = new ArrayList<>();

    // homework
    @ElementCollection
    private List<Long>  homeworksIds = new ArrayList<>();

    // completed homeworks
    @ElementCollection
    private List<Long> completedHomeworksIds = new ArrayList<>();

    // created homeworks
    @ElementCollection
    private List<Long> createdHomeworksIds = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    public boolean hasAdminRole() {
        return role != null && role.getName().equals("ADMIN");
    }

}
