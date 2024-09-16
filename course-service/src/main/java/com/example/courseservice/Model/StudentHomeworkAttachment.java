package com.example.courseservice.Model;

import com.example.courseservice.Dto.Homework.Enums.StudentAttachmentStatus;
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

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "homework_id")
        private Homework homework;

        @Column(name = "student_id")
        private Long studentId;

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @JoinColumn(name = "student_homework_attachment_id")
        private List<Attachment> attachments = new ArrayList<>();

        private String uploadedDate;

        // For teacher
        private StudentAttachmentStatus status;
        private Integer mark; // int cant be null so i make this variable Integer type
        private String message;
        private String checkedDate ;

    }