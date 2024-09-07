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
    private String role;

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

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_role",joinColumns = {@JoinColumn(name ="user_id",referencedColumnName ="id")},
            inverseJoinColumns ={@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private List<RoleEntity> roles = new ArrayList<>();
    public boolean hasAdminRole() {
        if (roles == null) {
            return false;
        }
        for (RoleEntity role : roles) {
            if (role.getName().equals("ADMIN")) {
                return true;
            }
        }
        return false;
    }

}
