package com.example.courseservice.Dto.StudenHomeworkAttachment;

import com.example.courseservice.Dto.Attachment.AttachmentDto;
import com.example.courseservice.Dto.Homework.Enums.StudentAttachmentStatus;
import com.example.courseservice.Model.Homework;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Lob;
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

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<AttachmentDto> attachments;


    private String uploadedDate;

    // For teacher
    private StudentAttachmentStatus status;
    private Integer mark;
    private String message;
    private String checkedDate ;


}

