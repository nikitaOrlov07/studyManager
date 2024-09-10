package com.example.courseservice.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    // Who  submit homework
    @ElementCollection
    List<Long> submitHomeworkUserEntitiesId = new ArrayList<Long>();

    // Graded homework
    @ElementCollection
    List<Long> gradedHomeworkUserEntitiesId = new ArrayList<>();

    // Rejected homework
    @ElementCollection
    List<Long> rejectedHomeworkUserEntitiesId = new ArrayList<>();

    // Homework Attachments
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "homework_id")
    @JsonIgnore
    private List<Attachment> attachmentList = new ArrayList<>();

    // Student homework attachment
    @OneToMany(mappedBy = "homework", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<StudentHomeworkAttachment> studentAttachments = new ArrayList<>();

    // Relationship with Course
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id") // This will create a foreign key in the Homework table
    @JsonIgnore
    private Course course;

    private Long authorId;

    // Homework status
    private HomeworkStatus status; // if all students attachments are checked -> homework is checked , if noy -> unchecked

}
