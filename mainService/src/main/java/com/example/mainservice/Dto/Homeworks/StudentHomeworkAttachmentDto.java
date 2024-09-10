package com.example.mainservice.Dto.Homeworks;


import com.example.mainservice.Dto.Homeworks.Enums.StudentAttachmentStatus;
import com.example.mainservice.Model.Attachment;
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
    private HomeworkDto homework;
    private Long studentId;
    private List<Attachment> attachments = new ArrayList<>();

    private String uploadedDate;

    // For teacher
    private StudentAttachmentStatus status;
    private Integer mark;
    private String message;
    private String checkedDate ;

}

