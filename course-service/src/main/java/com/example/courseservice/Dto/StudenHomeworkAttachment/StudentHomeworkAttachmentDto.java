package com.example.courseservice.Dto.StudenHomeworkAttachment;

import com.example.courseservice.Dto.Attachment.AttachmentDto;
import com.example.courseservice.Dto.Homework.HomeworkStatus;
import com.example.courseservice.Model.Attachment;
import com.example.courseservice.Model.Homework;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentHomeworkAttachmentDto {

    private Long id;
    private Homework homework;
    private Long studentId;
    private List<AttachmentDto> attachments = new ArrayList<>();

    private String uploadedDate;

    // For teacher
    private HomeworkStatus status;
    private Integer mark;
    private String message;
    private String checkedDate ;

}

