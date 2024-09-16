package com.example.mainservice.Dto.Homeworks;

import com.example.mainservice.Dto.Homeworks.Enums.HomeworkStatus;
import com.example.mainservice.Dto.Homeworks.Enums.StudentAttachmentStatus;
import com.example.mainservice.Dto.StudentAttachments.StudentHomeworkAttachmentDto;
import com.example.mainservice.Model.Attachment;
import com.example.mainservice.Model.Course;
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

    // Who  submit homework
    List<Long> submitHomeworkUserEntitiesId = new ArrayList<Long>();

    // Accepted homework (without grade)
    List<Long> acceptedHomeworkEntitiesId = new ArrayList<>();

    // Graded homework
    List<Long> gradedHomeworkUserEntitiesId = new ArrayList<>();

    // Rejected homework
    List<Long> rejectedHomeworkUserEntitiesId = new ArrayList<>();

    // Homework Attachments
    private List<Attachment> attachmentList = new ArrayList<>();

    // Student Attachments
    private List<StudentHomeworkAttachmentDto> studentAttachments = new ArrayList<>();

    // Relationship with Course
    private Course course;
    private Long authorId;

    // Homework status
    private HomeworkStatus status; // if all students attachments are checked -> homework is checked , if noy -> unchecked
}
