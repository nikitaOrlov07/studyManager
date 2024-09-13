package com.example.mainservice.Dto.StudentAttachments;

import com.example.mainservice.Dto.Homeworks.Enums.StudentAttachmentStatus;
import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Model.Attachment;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttachmentRequest {

    private Long homeworkId;
    private Long studentId;

    private List<MultipartFile> files = new ArrayList<>();

    private String uploadedDate;

    // For teacher
    private StudentAttachmentStatus status;
    private Integer mark;
    private String message;
    private String checkedDate ;
}
