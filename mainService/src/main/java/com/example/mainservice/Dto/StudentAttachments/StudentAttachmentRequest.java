package com.example.mainservice.Dto.StudentAttachments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttachmentRequest {

    private Long homeworkId;
    private Long studentId;

}
