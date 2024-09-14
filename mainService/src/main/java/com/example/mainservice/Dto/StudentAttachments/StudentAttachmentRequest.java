package com.example.mainservice.Dto.StudentAttachments;

import com.example.mainservice.Dto.Homeworks.Enums.StudentAttachmentStatus;
import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Model.Attachment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttachmentRequest {

    private Long homeworkId;
    private Long studentId;

}
