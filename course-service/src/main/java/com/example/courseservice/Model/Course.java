package com.example.courseservice.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Double price;
    private String language;
    @ElementCollection
    private List<String> tags;
    private String format;

    // Dates
    private String creationDate;
    private String endDate;

    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonManagedReference
    private List<Attachment> attachments = new ArrayList<>(); // for attachments (foto or videos)

    @ElementCollection
    private List<Long> involvedUserIds = new ArrayList<>();   // for users
    @ElementCollection
    private List<Long> requestedUsers = new ArrayList<>();  // for requested users

    private Long chatId; // for course main chat

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Homework> homeworks = new ArrayList<>(); // for homeworks
}