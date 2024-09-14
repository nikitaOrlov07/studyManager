package com.example.courseservice.Dto.StudenHomeworkAttachment;

import com.example.courseservice.Dto.Homework.Enums.StudentAttachmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAttachmentRequest {

    private Long homeworkId;
    private Long studentId;

    private List<MultipartFile> files = new ArrayList<>();

}
