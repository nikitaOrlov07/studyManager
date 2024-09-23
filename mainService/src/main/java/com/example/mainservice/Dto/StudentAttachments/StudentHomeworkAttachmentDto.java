package com.example.mainservice.Dto.StudentAttachments;


import com.example.mainservice.Dto.Homeworks.Enums.StudentAttachmentStatus;
import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Dto.course.Attachment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentHomeworkAttachmentDto {

    private Long id;
    private HomeworkDto homework;
    private Long studentId;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<Attachment> attachments;

    private String uploadedDate;

    // For teacher
    private StudentAttachmentStatus status;
    private Integer mark;
    private String message;
    private String checkedDate ;

}

