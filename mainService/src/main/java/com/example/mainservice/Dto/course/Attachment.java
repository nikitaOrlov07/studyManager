package com.example.mainservice.Dto.course;

import com.example.mainservice.Dto.course.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    private Long id;
    private String fileName;
    private String fileType;
    private String creator;
    private String downloadUrl;
    private String viewUrl;
    private String timestamp;
    private byte[] data;
    private Course course;
    private String accessType; // can be open or private

}
