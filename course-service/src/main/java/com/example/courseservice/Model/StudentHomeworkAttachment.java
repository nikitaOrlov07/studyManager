package com.example.courseservice.Model;

import com.example.courseservice.Dto.Homework.HomeworkStatus;
import com.example.courseservice.Model.Attachment;
import com.example.courseservice.Model.Homework;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class StudentHomeworkAttachment {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "homework_id")
        private Homework homework;

        @Column(name = "student_id")
        private Long studentId;

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @JoinColumn(name = "student_homework_attachment_id")
        private List<Attachment> attachments = new ArrayList<>();

        private String uploadedDate;

        // For teacher
        private HomeworkStatus status;
        private Integer mark;
        private String message;
        private String checkedDate ;

    }