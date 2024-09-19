package com.example.courseservice.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @Column(columnDefinition = "Text")
    private String description;
    private Double price;
    private String language;
    @ElementCollection
    private List<String> tags= new ArrayList<String>();
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

    private Long authorId;

    // Course "security"
    private String courseType;
    private String coursePassword;
    @Column(unique = true)
    private String courseKey;
    @PrePersist
    protected void onCreate() {
        if (this.courseKey == null) {
            this.courseKey = UUID.randomUUID().toString();
        }
    }
}