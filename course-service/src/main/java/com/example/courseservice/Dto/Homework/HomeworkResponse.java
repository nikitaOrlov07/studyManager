package com.example.courseservice.Dto.Homework;

import com.example.courseservice.Dto.Attachment.AttachmentDto;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import com.example.courseservice.Model.Attachment;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeworkResponse {
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
    private List<AttachmentDto> attachmentList = new ArrayList<>();

    // Student Attachments
    private List<StudentHomeworkAttachmentDto> studentAttachments = new ArrayList<>();

    // Relationship with Course
    private Course course;
    private Long authorId;

}
