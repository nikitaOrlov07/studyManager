package com.example.courseservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // basic information
    private String title;
    private String description;

    // Duration
    private String startDate;
    private String endDate;

    // Who need to do homework
    @ElementCollection
    List<Long> userEntitiesId = new ArrayList<Long>();

    // Homework Attachments
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "homework_id") // This will create a foreign key in the Attachment table
    private List<Attachment> attachmentList = new ArrayList<>();

    // Student Attachments
    @OneToMany(mappedBy = "homework", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentHomeworkAttachment> studentAttachments = new ArrayList<>();

    // Relationship with Course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id") // This will create a foreign key in the Homework table
    private Course course;

    private Long authorId;

}
