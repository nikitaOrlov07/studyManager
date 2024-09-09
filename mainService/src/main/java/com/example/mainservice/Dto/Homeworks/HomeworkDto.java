package com.example.mainservice.Dto.Homeworks;

import com.example.mainservice.Model.Attachment;
import com.example.mainservice.Model.Course;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkDto {

    private Long id;

    // basic information
    private String title;
    private String description;

    // Duration
    private String startDate;
    private String endDate;

    // Who need to do homework
    List<Long> userEntitiesId = new ArrayList<Long>();

    // Homework Attachments
    private List<Attachment> attachmentList = new ArrayList<>();

    // Student homework attachment
    @OneToMany(mappedBy = "homework", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<StudentHomeworkAttachmentDto> studentAttachments = new ArrayList<>();

    // Relationship with Course
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id") // This will create a foreign key in the Homework table
    @JsonIgnore
    private Course course;

    private Long authorId;
}
